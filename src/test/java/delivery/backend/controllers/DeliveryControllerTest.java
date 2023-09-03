package delivery.backend.controllers;

import delivery.backend.DeliveryApplicationTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeliveryControllerTest extends DeliveryApplicationTestBase {

    @Test
    void testEmptyDatabaseError() throws Exception {

        MvcResult resTallinn = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", CAR)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(WEATHERDATANOTFOUNDERROR, resTallinn.getResponse().getContentAsString());
    }

    @Test
    void testGetFeeUsingLatestWeatherData() throws Exception {

        saveDifferentDatesDifferentWeather();

        // 6 different dates with different weather conditions
        // Latest has air temperature of -15'C
        MvcResult resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinnBike.getResponse().getContentAsString());
    }

    @Test
    void testWrongStationError() throws Exception {
        saveGoodWeatherDataToDatabase();
        MvcResult resTallinn = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", NARVA)
                        .param("vehicle", CAR)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(STATIONNOTFOUNDERROR, resTallinn.getResponse().getContentAsString());
    }

    @Test
    void testVehicleTypeCarAllStations() throws Exception {

        saveGoodWeatherDataToDatabase();

        MvcResult resTallinn = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", CAR)
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinn.getResponse().getContentAsString());

        MvcResult resTartu = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TARTU)
                        .param("vehicle", CAR)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("3.5 €", resTartu.getResponse().getContentAsString());

        MvcResult resParnu = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", PARNU)
                        .param("vehicle", CAR)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("3.0 €", resParnu.getResponse().getContentAsString());
    }

    @Test
    void testExtraFeeBadWeather() throws Exception {

        saveBadWeatherDataToDatabase();

        // Tallinn - Rain and -2'C
        MvcResult resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinnBike.getResponse().getContentAsString());

        // Tartu - wind between 10m/s and 20m/s and snow
        MvcResult resTartuBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TARTU)
                        .param("vehicle", BIKE)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTartuBike.getResponse().getContentAsString());

        // Pärnu - -20'C
        MvcResult resParnuScooter = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", PARNU)
                        .param("vehicle", SCOOTER)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("3.5 €", resParnuScooter.getResponse().getContentAsString());
    }

    @Test
    void testForbiddenVehicleTypeForWeather() throws Exception {

        saveWorseWeatherDataToDatabase();

        // Tallinn - Thunder
        MvcResult resTallinnScooter = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", SCOOTER)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(FORBIDDENERROR, resTallinnScooter.getResponse().getContentAsString());


        // Tartu - glaze and hail
        MvcResult resTartuScooter = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TARTU)
                        .param("vehicle", SCOOTER)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(FORBIDDENERROR, resTartuScooter.getResponse().getContentAsString());

        // Pärnu - wind speed > 20m/s
        MvcResult resParnuBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", PARNU)
                        .param("vehicle", BIKE)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(FORBIDDENERROR, resParnuBike.getResponse().getContentAsString());

        // Tallinn - Thunder
        // Car can still make deliveries in bad weather
        MvcResult resTallinnCar = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", CAR)
                        .param("date", "")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinnCar.getResponse().getContentAsString());
    }

    @Test
    void testChooseDateDifferentWeathers() throws Exception {

        saveDifferentDatesDifferentWeather();

        // Chosen date is 16/06/2023 14:09
        // Closest data should be 15/06/2023 15:15:00 - Rain
        MvcResult resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-06-16T14:09")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("3.5 €", resTallinnBike.getResponse().getContentAsString());

        // Chosen date is 29/06/2023 16:16
        // Closest data should be 30/06/2023 15:15:00 - Snow and -2'C
        resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-06-29T16:16")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.5 €", resTallinnBike.getResponse().getContentAsString());

        // Chosen date is 15/07/2023 11:30
        // Closest data should be 15/07/2023 11:15:00 - Thunder
        resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-07-15T11:30")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(FORBIDDENERROR, resTallinnBike.getResponse().getContentAsString());

        // Chosen date is 15/07/2023 11:45
        // Closest data should be 15/07/2023 12:15:00 - Wind speed is 15 m/s
        resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-07-15T11:45")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("3.5 €", resTallinnBike.getResponse().getContentAsString());

        // Chosen date is 31/07/2023 12:29
        // Closest data should be 30/07/2023 15:15:00 - Air Temperature is -2'C
        resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-07-31T12:29")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("3.5 €", resTallinnBike.getResponse().getContentAsString());

        // Chosen date is 14/08/2023 17:29
        // Closest data should be 15/08/2023 15:15:00 - Air Temperature is -15'C
        resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-08-14T17:29")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals("4.0 €", resTallinnBike.getResponse().getContentAsString());
    }

    @Test
    void testChooseDateErrors() throws Exception {

        saveDifferentDatesDifferentWeather();

        // Chosen date is 17/06/2023 17:09:42
        // Closest data should be 15/06/2023 15:15:00, but dates are more than 1 day apart
        MvcResult resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", "2023-06-17T17:01")
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(DATESTOOFARAPARTERROR, resTallinnBike.getResponse().getContentAsString());

        // Chosen date is always Tomorrow
        LocalDateTime chosenDate = LocalDateTime.now().plusDays(1);
        resTallinnBike = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delivery")
                        .param("station", TALLINN)
                        .param("vehicle", BIKE)
                        .param("date", chosenDate.toString())
                        .content(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(FUTUREDATEERROR, resTallinnBike.getResponse().getContentAsString());
    }
}
