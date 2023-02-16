package it.pagopa.pn.delivery.exception;

import it.pagopa.pn.commons.exceptions.PnExceptionsCodes;

public class PnDeliveryExceptionCodes extends PnExceptionsCodes {

    // raccolgo qui tutti i codici di errore di delivery
    public static final String ERROR_CODE_DELIVERY_MANDATENOTFOUND = "PN_DELIVERY_MANDATENOTFOUND";
    public static final String ERROR_CODE_DELIVERY_NOTIFICATIONNOTFOUND = "PN_DELIVERY_NOTIFICATIONNOTFOUND";
    public static final String ERROR_CODE_DELIVERY_FILEINFONOTFOUND = "PN_DELIVERY_FILEINFONOTFOUND";
    public static final String ERROR_CODE_DELIVERY_NOTIFICATIONCOSTNOTFOUND = "PN_DELIVERY_NOTIFICATIONCOSTNOTFOUND";
    public static final String ERROR_CODE_DELIVERY_USER_ID_NOT_RECIPIENT_OR_DELEGATOR = "PN_DELIVERY_USER_ID_NOT_RECIPIENT_OR_DELEGATOR";
    public static final String ERROR_CODE_DELIVERY_UNSUPPORTED_AAR_QR_CODE = "PN_DELIVERY_UNSUPPORTED_AAR_QR_CODE";

    public static final String ERROR_CODE_DELIVERY_NOTIFICATIONWITHOUTPAYMENTATTACHMENT = "PN_DELIVERY_NOTIFICATIONWITHOUTPAYMENTATTACHMENT";

    public static final String ERROR_CODE_DELIVERY_INVALIDPARAMETER_GROUP = "PN_DELIVERY_INVALIDPARAMETER_GROUP";
    public static final String ERROR_CODE_DELIVERY_UNSUPPORTED_CX_TYPE = "PN_DELIVERY_UNSUPPORTED_CX_TYPE";
    public static final String ERROR_CODE_DELIVERY_UNSUPPORTED_PHYSICALCOMMUNICATIONTYPE = "PN_DELIVERY_UNSUPPORTED_PHYSICALCOMMUNICATIONTYPE";
    public static final String ERROR_CODE_DELIVERY_UNSUPPORTED_INDEX_NAME = "PN_DELIVERY_UNSUPPORTED_INDEX_NAME";
    public static final String ERROR_CODE_DELIVERY_UNSUPPORTED_LAST_EVALUATED_KEY = "PN_DELIVERY_UNSUPPORTED_LAST_EVALUATED_KEY";
    public static final String ERROR_CODE_DELIVERY_UNSUPPORTED_NOTIFICATION_METADATA = "PN_DELIVERY_UNSUPPORTED_NOTIFICATION_METADATA";

    public static final String ERROR_CODE_DELIVERY_HANDLEEVENTFAILED = "PN_DELIVERY_HANDLEEVENTFAILED";

}
