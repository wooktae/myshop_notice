package myShop;

import myShop.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    NoticeRepository noticeRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryCanceled_CancelNoticeRequest(@Payload DeliveryCanceled deliveryCanceled){

        if(deliveryCanceled.isMe()){
            System.out.println("##### listener CancelNoticeRequest : " + deliveryCanceled.toJson());

            Notice notice = new Notice();
            // mappings goes here
            notice.setOrderId(deliveryCanceled.getOrderId());
            notice.setNotiStatus("Cancel Notice Send");

            noticeRepository.save(notice);

        }
    }

}
