package org.hringsak.functions.consumer;

import org.hringsak.functions.mapper.MapperUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Methods that build consumers useful in many different situations, particularly in Java streams.
 */
public final class ConsumerUtils {

    private ConsumerUtils() {
    }

    /**
     * Simply casts a method reference, which takes a single parameter of type &lt;T&gt; and returns void, to a <code>
     * Consumer</code>. This could be useful in a situation where methods of the <code>Consumer</code> interface are to
     * be called on a method reference. In the following example, assume that <code>methodOne()</code> and <code>
     * methodTwo()</code> are methods in the current class, and both take a single parameter of type <code>Widget</code>:
     * <pre>
     *     Collection&lt;Widget&gt; widgets = ...
     *     widgets.forEach(ConsumerUtils.consumer(this::methodOne)
     *             .andThen(this::methodTwo));
     * </pre>
     * Or, with static imports:
     * <pre>
     *     widgets.forEach(consumer(this::methodOne)
     *             .andThen(this::methodTwo));
     * </pre>
     * The <code>Consumer.andThen()</code> method can only be called on the method reference because of the cast.
     *
     * @param consumer A method reference to be cast to a Consumer.
     * @param <T>      The type of the single parameter to the Consumer.
     * @return A method reference cast to a Consumer.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static <T> Consumer<T> consumer(Consumer<T> consumer) {
        return consumer;
    }

    /**
     * Simply casts a method reference, which takes no parameters and returns void, to a <code>Consumer</code>. This
     * could be useful in a situation where you have a method that takes no parameters, and has no return value, which
     * you would like to call in a stream, for example, in the <code>Stream.forEach(...)</code> method. In the following
     * example, assume that <code>processWidget()</code> and <code>saveWidget</code> both take a single
     * <code>Widget</code> parameter and have no return value, and <code>logCurrentState()</code> takes no parameter and
     * has no return value:
     * <pre>
     *     Collection&lt;Widget&gt; widgets = ...
     *     widgets.forEach(ConsumerUtils.consumer(this::processWidget)
     *             .andThen(ConsumerUtils.consumer(this::logCurrentState))
     *             .andThen(this::saveWidget));
     * </pre>
     * Or, with static imports:
     * <pre>
     *     widgets.forEach(consumer(this::processWidget)
     *             .andThen(consumer(this::logCurrentState))
     *             .andThen(this::saveWidget));
     * </pre>
     * Admittedly, the fact that we are using <code>forEach(...)</code> here, using object state for the logging, and
     * not returning any values, makes this code imperative, and not functional. However, casting a
     * <code>Runnable</code> to a <code>Consumer</code> does come in handy at times.
     * <p>
     * Note that this method can also be used to cast a <code>Supplier</code> method reference to a
     * <code>Consumer</code>, that is a reference to a method that takes no parameters, and returns an object of any
     * type.
     *
     * @param runnable A method reference taking no parameters and having a return value of any type, including no
     *                 return value, to be cast to a Consumer.
     * @param <T>      The type of the single parameter to the Consumer.
     * @return A Runnable or Supplier method reference cast to a Consumer.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static <T> Consumer<T> consumer(Runnable runnable) {
        return t -> runnable.run();
    }

    /**
     * Builds a consumer from a passed <code>BiConsumer</code>, which can be very useful in the common situation where
     * you are streaming through a collection of elements, and have a method to call that takes two parameters - the
     * first one being the element on which you are streaming, and the second being some constant value that will be
     * passed to all invocations. This would typically be called from within the <code>Collection.forEach(...)</code>
     * or <code>Stream.forEach(...)</code> method. Another example of a method that requires a <code>Consumer</code> is
     * the <code>Optional.ifPresent(...)</code> method. The following is a contrived example, but it illustrates the use
     * of the method. Assume we have a collection of persistent entities that we have manipulated somehow, and now they
     * need to be saved to a database. Also assume the <code>WidgetRepository.saveWidget()</code> method takes a <code>
     * Widget</code> as the first parameter, and a <code>String</code> projectId as a second parameter:
     * <pre>
     *     private void saveWidgets(Collection&lt;Widget&gt; widgets, String projectId) {
     *         widgets.forEach(ConsumerUtils.consumer(widgetRepository::saveWidget, projectId));
     *     }
     *
     *     private void saveWidget(Widget widget, String projectId) {
     *         ...
     *     }
     * </pre>
     * Or, with static imports:
     * <pre>
     *         widgets.forEach(consumer(widgetRepository::saveWidget, projectId));
     * </pre>
     * In this example, we have each widget to be saved being passed to the save method, along with a projectId, which
     * remains constant for every call.
     * <p>
     * One note about using the Java <code>Consumer</code> interface, as it says in the Javadoc documentation for it,
     * "Unlike most other functional interfaces, Consumer is expected to operate via side-effects."
     *
     * @param biConsumer A method reference which is a BiConsumer, taking two parameters - the first of type &lt;T&gt;,
     *                   and the second of type &lt;U&gt;, either of which can be any type. The method reference will be
     *                   converted by this method to a Consumer, taking a single parameter of type &lt;T&gt;. Behind the
     *                   scenes, this BiConsumer will be called, passing the constant value to each invocation as the
     *                   second parameter.
     * @param value      A constant value, in that it will be passed to every invocation of the passed biConsumer as the
     *                   second parameter to it, and will have the same value for each of them.
     * @param <T>        The target type of the first parameter to the passed biConsumer.
     * @param <U>        The type of the constant value to be passed as the second parameter to each invocation of
     *                   biConsumer.
     * @return A Consumer taking a single parameter of type &lt;T&gt;.
     */
    public static <T, U> Consumer<T> consumer(BiConsumer<? super T, ? super U> biConsumer, U value) {
        return t -> biConsumer.accept(t, value);
    }

    /**
     * As in the {@link #consumer(BiConsumer, Object)} method, builds a consumer from a passed <code>BiConsumer</code>,
     * which can be very useful in the common situation where you are streaming through a collection elements, and have
     * a method to call that takes two parameters. In the <code>BiConsumer</code> passed to this method, the parameters
     * are basically the same as in {@link #consumer(BiConsumer, Object)}, but in the inverse order. Here, the first
     * parameter is a constant value that will be passed to all invocations of the method, and the second parameter is
     * the element on which you are streaming. This would typically be called from within the <code>Collection.forEach(...)
     * </code> or <code>Stream.forEach(...)</code> method. Another example of a method that requires a <code>Consumer</code>
     * is the <code>Optional.ifPresent(...)</code> method. The following is a contrived example, but it illustrates the
     * use of the method. Assume we have a collection of persistent entities that we have manipulated somehow, and now
     * they need to be saved to a database. Also assume the <code>WidgetRepository.saveWidget()</code> method takes a
     * <code>String</code> projectId as the first parameter, and a <code>Widget</code> as the second parameter:
     * <pre>
     *     private void saveWidgets(Collection&lt;Widget&gt; widgets, String projectId) {
     *         widgets.forEach(ConsumerUtils.inverseConsumer(widgetRepository::saveWidget, projectId));
     *     }
     *
     *     private void saveWidget(String projectId, Widget widget) {
     *         ...
     *     }
     * </pre>
     * Or, with static imports:
     * <pre>
     *         widgets.forEach(inverseConsumer(widgetRepository::saveWidget, projectId));
     * </pre>
     * This example looks almost exactly the same as the one in {@link #consumer(BiConsumer, Object)}, but the difference
     * is that the order of the parameters in the passed <code>BiConsumer</code> method reference are reversed. So the
     * parameters to the widgetRepository.saveWidget(...) method would be <code>projectId</code>, and then the <code>
     * widget</code> as the second parameter.
     * <p>
     * One note about using the Java <code>Consumer</code> interface, as it says in the Javadoc documentation for it,
     * "Unlike most other functional interfaces, Consumer is expected to operate via side-effects."
     *
     * @param biConsumer A method reference which is a BiConsumer, taking two parameters - the first of type &lt;U&gt;,
     *                   and the second of type &lt;T&gt;, either of which can be any type. The method reference will be
     *                   converted by this method to a Consumer, taking a single parameter of type &lt;T&gt;. Behind the
     *                   scenes, this BiConsumer will be called, passing the constant value to each invocation as the
     *                   first parameter.
     * @param value      A constant value, in that it will be passed to every invocation of the passed biConsumer as the
     *                   first parameter to it, and will have the same value for each of them.
     * @param <T>        The target type of the second parameter to the passed biConsumer.
     * @param <U>        The type of the constant value to be passed as the first parameter to each invocation of
     *                   biConsumer.
     * @return A Consumer taking a single parameter of type &lt;T&gt;.
     */
    public static <T, U> Consumer<T> inverseConsumer(BiConsumer<? super U, ? super T> biConsumer, U value) {
        return t -> biConsumer.accept(value, t);
    }

    /**
     * This method is a variation of the <code>consumer(...)</code> methods that has some special properties. First, it
     * is assumed that a setter method is being called on the target element, so there is a null check to make sure that
     * it is an object instance. If not, the <code>Consumer</code> built by this method simply returns. Second, it takes
     * a function that, given the target element, it returns the value to be passed to the setter. This  means that any
     * of the methods of {@link MapperUtils} are fair game to be used to build that function. For example, suppose we
     * want to write a method that, given a collection of <code>Widget</code> instances, it will build and set a
     * description for each of them. We could use this method to help accomplish that in the following contrived example:
     * <pre>
     *     private void buildWidgetDescriptions(Collection&lt;Widget&gt; widgets) {
     *         String productLine = formatProductLine();
     *         widgets.forEach(ConsumerUtils.setter(Widget::setDescription, MapperUtils.mapper(this::buildDescription, productLine)));
     *     }
     *
     *     private String buildDescription(Widget widget, String productLine) {
     *         ...
     *     }
     * </pre>
     * Or, with static imports:
     * <pre>
     *         widgets.forEach(setter(Widget::setDescription, mapper(this::buildDescription, productLine)));
     * </pre>
     *
     * @param consumer A setter method reference from the class of the target element. It is a BiConsumer because the
     *                 first parameter will be the element itself, and the second parameter is the value to be set on
     *                 it.
     * @param function An extractor function that, given the target element, returns the value to be set.
     * @param <T>      The type of the target input element.
     * @param <U>      The type of the parameter to be passed to the setter method.
     * @return A Consumer representing the invocation of a setter method on a target element.
     */
    public static <T, U> Consumer<T> setter(BiConsumer<? super T, ? super U> consumer, Function<T, U> function) {
        return t -> {
            if (t != null) {
                U value = function.apply(t);
                consumer.accept(t, value);
            }
        };
    }

    /**
     * Applies a <code>Function</code> to a target element, before passing its result to a <code>Consumer</code>. For
     * example, let's say that we have a collection of order line items, and we want to call a validation method to make
     * sure that the current customer is able to order items from the product type of the current line item:
     * <pre>
     *     private void validateLineItems(Collection&lt;OrderLineItem&gt; lineItems, String customerId) {
     *         lineItems.forEach(ConsumerUtils.mapAndConsume(OrderLineItem::getProductType, ConsumerUtils.consumer(this::validateProductType, customerId)));
     *     }
     *
     *     private String validateProductType(ProductType productType, String customerId) {
     *         ...
     *     }
     * </pre>
     * Or, with static imports:
     * <pre>
     *         lineItems.forEach(mapAndConsume(OrderLineItem::getProductType, consumer(this::validateProductType, customerId)));
     * </pre>
     * Note that the same thing could be done like this:
     * <pre>
     *         lineItems.stream()
     *             .map(OrderLineItem::getProductType)
     *             .forEach(consumer(this::validateProductType, customerId));
     * </pre>
     * Which of the above is more concise and readable is up to the individual developer, but this method provides an
     * alternative way of accomplishing the above validation.
     *
     * @param function A Function to be applied to a target element.
     * @param consumer A Consumer to be applied to the result of a Function.
     * @param <T>      The type of the target input element.
     * @param <U>      The type of the result of a Function, which will be passed to a Consumer.
     * @return A Consumer which will be invoked, passing the result of a Function.
     */
    public static <T, U> Consumer<T> mapAndConsume(Function<? super T, ? extends U> function, Consumer<? super U> consumer) {
        return t -> {
            if (t != null) {
                U value = function.apply(t);
                consumer.accept(value);
            }
        };
    }
}
