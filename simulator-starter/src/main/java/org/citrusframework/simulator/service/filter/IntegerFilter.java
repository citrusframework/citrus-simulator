package org.citrusframework.simulator.service.filter;

/**
 * Filter class for {@link java.lang.Integer} type attributes.
 *
 * @see RangeFilter
 */
public class IntegerFilter extends RangeFilter<Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for IntegerFilter.</p>
     */
    public IntegerFilter() {
    }

    /**
     * <p>Constructor for IntegerFilter.</p>
     *
     * @param filter a {@link IntegerFilter} object.
     */
    public IntegerFilter(IntegerFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link IntegerFilter} object.
     */
    public IntegerFilter copy() {
        return new IntegerFilter(this);
    }
}
