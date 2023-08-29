package delivery.backend;

import delivery.backend.controllers.DeliveryController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FujitsuTrialTaskApplicationTests extends DeliveryApplicationTestBase {

    @Autowired
    private DeliveryController controller;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(controller);
    }

}
