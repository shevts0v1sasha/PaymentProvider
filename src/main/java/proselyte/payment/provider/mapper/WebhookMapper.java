package proselyte.payment.provider.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import proselyte.payment.provider.dto.WebhookDto;
import proselyte.payment.provider.entity.WebhookEntity;

@Mapper(componentModel = "spring")
public interface WebhookMapper {

    WebhookDto map(WebhookEntity webhookEntity);

    @InheritInverseConfiguration
    WebhookEntity map(WebhookDto webhookDto);
}
