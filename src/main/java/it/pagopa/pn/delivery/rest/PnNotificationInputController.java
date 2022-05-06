package it.pagopa.pn.delivery.rest;

import it.pagopa.pn.commons.exceptions.PnValidationException;
import it.pagopa.pn.delivery.PnDeliveryConfigs;
import it.pagopa.pn.delivery.generated.openapi.server.v1.api.NewNotificationApi;
import it.pagopa.pn.delivery.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.delivery.models.InternalNotification;
import it.pagopa.pn.delivery.models.InternalNotificationSender;
import it.pagopa.pn.delivery.rest.dto.ConstraintViolationImpl;
import it.pagopa.pn.delivery.rest.dto.ResErrorDto;
import it.pagopa.pn.delivery.rest.utils.HandleValidation;
import it.pagopa.pn.delivery.svc.NotificationReceiverService;
import it.pagopa.pn.delivery.svc.S3PresignedUrlService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class PnNotificationInputController implements NewNotificationApi {

    public static final String NOTIFICATION_VALIDATION_ERROR_STATUS = "Notification validation error";
    private final PnDeliveryConfigs cfgs;
    private final NotificationReceiverService svc;
    private final S3PresignedUrlService presignSvc;

    public PnNotificationInputController(PnDeliveryConfigs cfgs, NotificationReceiverService svc, S3PresignedUrlService presignSvc) {
        this.cfgs = cfgs;
        this.svc = svc;
        this.presignSvc = presignSvc;
    }

    @Override
    public ResponseEntity<NewNotificationResponse> sendNewNotification(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<String> xPagopaPnCxGroups, NewNotificationRequest newNotificationRequest) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        modelMapper.createTypeMap( newNotificationRequest.getClass(), InternalNotification.class )
                .addMapping(NewNotificationRequest::getPaProtocolNumber, InternalNotification::setPaNotificationId )
                .addMapping(NewNotificationRequest::getSubject, InternalNotification::setSubject);

        InternalNotification internalNotification = modelMapper.map(newNotificationRequest, InternalNotification.class);

        internalNotification.setSender( InternalNotificationSender.builder()
                        .paId( xPagopaPnCxId )
                        .taxId( newNotificationRequest.getSenderTaxId() )
                        .paDenomination( newNotificationRequest.getSenderDenomination() )
                .build() );
        log.info( internalNotification.toString() );

        NewNotificationResponse svcRes = svc.receiveNotification(internalNotification);
        return ResponseEntity.ok( svcRes );
    }


    @Override
    public ResponseEntity<List<PreLoadResponse>> presignedUploadRequest(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, List<PreLoadRequest> preLoadRequest) {
        Integer numberOfPresignedRequest = cfgs.getNumberOfPresignedRequest();
        if ( preLoadRequest.size() > numberOfPresignedRequest ) {
            log.error( "Presigned upload request lenght={} is more than maximum allowed={}",
                    preLoadRequest.size(), numberOfPresignedRequest );
            throw new PnValidationException("request",
                    Collections.singleton( new ConstraintViolationImpl<>(
                            String.format( "request.length = %d is more than maximum allowed = %d",
                                    preLoadRequest.size(),
                                    numberOfPresignedRequest))));
        }
        List<PreLoadResponse> preLoadResponses = presignSvc.presignedUpload( xPagopaPnCxId, preLoadRequest );
        return ResponseEntity.ok( preLoadResponses );
    }

    /*@Override
    @PostMapping(PnDeliveryRestConstants.SEND_NOTIFICATIONS_PATH )
    public NewNotificationResponse receiveNotification(
            @RequestHeader(name = PnDeliveryRestConstants.CX_ID_HEADER ) String paId,
            @RequestBody @JsonView(value = NotificationJsonViews.New.class ) Notification notification
    ) {
        if( notification.getPhysicalCommunicationType() == null ) {
            log.warn( "Add default physical communication type for paNotificationId={} from paId={}",
                    notification.getPaNotificationId(), paId );
            notification = notification.toBuilder()
                    .physicalCommunicationType(ServiceLevelType.REGISTERED_LETTER_890)
                    .build();
        }

        Notification withSender = notification.toBuilder()
                .sender( NotificationSender.builder()
                        .paId( paId )
                        .build()
                )
                .build();

        return svc.receiveNotification( withSender );
    }

    @Override
    @PostMapping( PnDeliveryRestConstants.ATTACHMENT_PRELOAD_REQUEST)
    public List<PreloadResponse> presignedUploadRequest(
            @RequestHeader(name = PnDeliveryRestConstants.CX_ID_HEADER ) String paId,
            @RequestBody List<PreloadRequest> request
    ) {
        Integer numberOfPresignedRequest = cfgs.getNumberOfPresignedRequest();
        if ( request.size() > numberOfPresignedRequest ) {
            log.error( "Presigned upload request lenght={} is more than maximum allowed={}",
                    request.size(), numberOfPresignedRequest );
            throw new PnValidationException("request",
                    Collections.singleton( new ConstraintViolationImpl<>(
                            String.format( "request.length = %d is more than maximum allowed = %d",
                                    request.size(),
                                    numberOfPresignedRequest))));
        }
        return presignSvc.presignedUpload( paId, request );
    }*/

    @ExceptionHandler({PnValidationException.class})
    public ResponseEntity<ResErrorDto> handleValidationException(PnValidationException ex){
        return HandleValidation.handleValidationException(ex,NOTIFICATION_VALIDATION_ERROR_STATUS );
    }
}
