package com.cloudshare.server.common.constant;

/**
 * @author novo
 * @since 2023/10/11
 */
public final class BizConstant {

    private BizConstant() {

    }

    public static final String LINUX_SEPARATOR = "/";

    public static final String DOT = ".";

    public static final String REPEAT_NAME = "重名冲突";

    public static final String SPACE_LIMIT = "空间不足";

    public static final Long ROOT_PARENT_ID = 0L;

    /**
     * 订单超时 30min
     * 单位 ms
     */
    public static final long PLACE_ORDER_TIME_OUT = 30 * 60 * 1000;

    public static final String ORDER_DELAY_QUEUE = "cloudshare:order:delay_cancel";

    public static final String ORDER_PAID_HANDLER_PREFIX = "cloudshare:order:paid:";

    public static final String SEARCH_HISTORY_PREFIX = "cloudshare:search_history:";

    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String LINK_PV_PREFIX = "cloudshare:pv:";
}
