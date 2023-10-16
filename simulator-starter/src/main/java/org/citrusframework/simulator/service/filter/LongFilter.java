package org.citrusframework.simulator.service.filter;

/**
 * Filter class for {@link java.lang.Long} type attributes.
 *
 * @see RangeFilter
 */
public class LongFilter extends RangeFilter<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for LongFilter.</p>
     */
    public LongFilter() {
    }

    /**
     * <p>Constructor for LongFilter.</p>
     *
     * @param filter a {@link LongFilter} object.
     */
    public LongFilter(LongFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link LongFilter} object.
     */
    public LongFilter copy() {
        return new LongFilter(this);
    }
}
