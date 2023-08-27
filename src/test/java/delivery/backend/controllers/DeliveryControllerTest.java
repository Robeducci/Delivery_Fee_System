package delivery.backend.controllers;

import delivery.backend.DeliveryApplicationTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeliveryControllerTest extends DeliveryApplicationTestBase {

    @Test
    void testEmptyDatabaseError() throws Exception {
        MvcResult resTallinn = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", CAR)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(ERRORANSWER, resTallinn.getResponse().getContentAsString());
    }

    @Test
    void testWrongStationError() throws Exception {
        saveGoodWeatherDataToDatabase();
        MvcResult resTallinn = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", NARVA)
                        .param("vehicle", CAR)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(ERRORANSWER, resTallinn.getResponse().getContentAsString());
    }

    @Test
    void testVehicleTypeCarAllStations() throws Exception {
        saveGoodWeatherDataToDatabase();
        MvcResult resTallinn = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", CAR)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resTartu = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TARTU)
                        .param("vehicle", CAR)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resParnu = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", PARNU)
                        .param("vehicle", CAR)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinn.getResponse().getContentAsString());
        Assertions.assertEquals("3.5 €", resTartu.getResponse().getContentAsString());
        Assertions.assertEquals("3.0 €", resParnu.getResponse().getContentAsString());
    }

    @Test
    void testExtraFeeBadWeather() throws Exception {

        // Tallinn - Rain and -2'C
        // Tartu - wind between 10m/s and 20m/s and snow
        // Pärnu - -20'C
        saveBadWeatherDataToDatabase();

        MvcResult resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resTartuBike = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TARTU)
                        .param("vehicle", BIKE)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resParnuScooter = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", PARNU)
                        .param("vehicle", SCOOTER)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinnBike.getResponse().getContentAsString());
        Assertions.assertEquals("4.0 €", resTartuBike.getResponse().getContentAsString());
        Assertions.assertEquals("3.5 €", resParnuScooter.getResponse().getContentAsString());
    }

    @Test
    void testFrobiddenVehicleTypeForWeather() throws Exception {

        // Tallinn - Thunder
        // Tartu - glaze and hail
        // Tartu - wind speed > 20m/s
        saveWorseWeatherDataToDatabase();

        MvcResult resTallinnScooter = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", SCOOTER)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resTartuScooter = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", TARTU)
                        .param("vehicle", SCOOTER)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult resParnuBike = mockMvc.perform(MockMvcRequestBuilders
                        .post("/delivery")
                        .param("station", PARNU)
                        .param("vehicle", BIKE)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(FORBIDDENERROR, resTallinnScooter.getResponse().getContentAsString());
        Assertions.assertEquals(FORBIDDENERROR, resTartuScooter.getResponse().getContentAsString());
        Assertions.assertEquals(FORBIDDENERROR, resParnuBike.getResponse().getContentAsString());
    }
}