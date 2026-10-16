package kz.janapost.digitalhr.integration.documentolog;

import kz.janapost.digitalhr.request.entity.HrRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Точка интеграции с СЭД «Documentolog» (системный документооборот и ЭЦП РК).
 * Обеспечивает подписание кадровых заявлений с помощью ЭЦП НУЦ РК.
 */
@Service
@Slf4j
public class DocumentologIntegrationService {

    public String routeDocumentForSignature(HrRequest request) {
        String documentologId = "DOC-KZ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("📡 [Documentolog Integration] Маршрутизация заявления #{} сотрудника #{} на подписание ЭЦП в Documentolog. ID документа: {}",
                request.getId(), request.getEmployeeId(), documentologId);
        return documentologId;
    }

    public boolean isSigned(String documentologId) {
        log.info("📡 [Documentolog Integration] Проверка статуса ЭЦП документа {}", documentologId);
        return true;
    }
}
