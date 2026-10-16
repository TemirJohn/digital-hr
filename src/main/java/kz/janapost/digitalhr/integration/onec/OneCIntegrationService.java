package kz.janapost.digitalhr.integration.onec;

import kz.janapost.digitalhr.request.entity.HrRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Точка интеграции с системой «1C:Зарплата и управление персоналом» (1C:ЗУП).
 * В соответствии с требованиями конкурса здесь размещён mock-адаптер для будущей интеграции через REST/SOAP API 1C.
 */
@Service
@Slf4j
public class OneCIntegrationService {

    public String exportOrderTo1C(HrRequest request) {
        String docNumber1C = "1C-ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("📡 [1C:ЗУП Integration] Экспорт приказа в 1С:ЗУП. Заявка #{} (тип: {}, сотрудник #{}) -> Документ 1С: {}",
                request.getId(), request.getType(), request.getEmployeeId(), docNumber1C);
        return docNumber1C;
    }

    public int fetchVacationDaysBalance(Long employeeId) {
        log.info("📡 [1C:ЗУП Integration] Запрос остатка отпускных дней из регистра 1C:ЗУП для сотрудника #{}", employeeId);
        // В реальной системе: вызов OData/SOAP сервиса 1С. По умолчанию базовый лимит 24 дня.
        return 24;
    }
}
