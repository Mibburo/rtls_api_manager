package pameas.rtls.api.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.javafaker.Faker;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pameas.rtls.api.manager.model.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.io.IOException;
import java.net.URI;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import static pameas.rtls.api.manager.utils.Constants.DBPROXY_URL;

//used for emulation
@Slf4j
@Service
public class RegistrationService {
    Random random = new Random();

    public String addPerson() throws UnirestException, IOException, InterruptedException {

        log.info("2222222222222222222 add person");
        String accessToken = TokenService.getAccessToken();
        PersonTO personTO = generatePerson();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(personTO);
        log.info("aaaaaaaaaaaaaaaaaaaaaaa json :{}", json);
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(DBPROXY_URL +"/addPerson/"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer "+accessToken)
//                .method("POST", HttpRequest.BodyPublishers.ofString(json))
//                .build();
//        log.info("bbbbbbbbbbbbbbbbbbbbb request :{}", request);
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        log.info("3333333333333333333333 response :{}", response);

        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",  "Bearer "+accessToken);

        HttpEntity<PersonTO> request = new HttpEntity<>(personTO, headers);
        String response = restTemplate.postForObject(DBPROXY_URL +"/addPerson/", request, String.class);
        log.info(response);


        return personTO.getIdentifier();

    }

    public void addDevice(String macAddress, String hashedMacAddress, String identifier) throws UnirestException, IOException, InterruptedException {
        String accessToken = TokenService.getAccessToken();
        Device device = generateDevice(macAddress, hashedMacAddress, identifier);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(device);
        log.info("ssssssssssssssssssss device :{}", json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DBPROXY_URL +"/addDevice/"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+accessToken)
                .method("POST", HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private PersonTO generatePerson(){
        PersonTO personTO = new PersonTO();
        Faker faker = new Faker();
        personTO.setName(faker.name().firstName());
        personTO.setSurname(faker.name().lastName());
        personTO.setGender(generateGender());
        personTO.setIdentifier(UUID.randomUUID().toString());
        personTO.setAge(String.valueOf(generateAge()));
        personTO.setConnectedPassengers(generatePassengers());
        personTO.setTicketNumber(UUID.randomUUID().toString());
        personTO.setEmail(personTO.getName() + personTO.getSurname() + "@email.com");
        personTO.setRole("passenger");
        personTO.setCrew(false);
        personTO.setMedicalCondition("");
        personTO.setPreferredLanguage(new String[]{"EN"});
        personTO.setEmbarkationPort("Piraeus");
        personTO.setDisembarkationPort("Chania");
        personTO.setCountryOfResidence("Greece");
        personTO.setMobilityIssues("");
        personTO.setPrengencyData("");
        personTO.setEmergencyDuty("");
        personTO.setPostalAddress(String.valueOf(random.ints(10000, 99999).findFirst().getAsInt()));
        personTO.setEmergencyContact(faker.name().fullName());
        personTO.setDutySchedule(new ArrayList<>());
        personTO.setInPosition(false);
        personTO.setAssignmentStatus(Personalinfo.AssignmentStatus.UNASSIGNED);
        personTO.setAssignedMusteringStation("7BG6");

        return personTO;
    }

    private Device generateDevice(String macAddress, String hashedMacAddress, String identifier){
        Device device = new Device();
        device.setIdentifier(identifier);
        device.setMacAddress(macAddress);
        device.setHashedMacAddress(hashedMacAddress);
        device.setImsi(String.valueOf(Math.random() * 100000000000000L).replace(".", ""));
        device.setImei(String.valueOf(Math.random() * 100000000000000L).replace(".", ""));
        device.setMsisdn("MSISDN");
        device.setMessagingAppClientId(hashedMacAddress);
        return device;
    }

    private List<DutySchedule> generateDutySchedule(String start, String end){
        DutySchedule schedule = new DutySchedule();
//        schedule.setDutyStartDateTime(start);
//        schedule.setDutyEndDateTime(end);
        schedule.setDutyStartDateTime(null);
        schedule.setDutyEndDateTime(null);

        List<DutySchedule> dutySchedules = new ArrayList<>();
        dutySchedules.add(schedule);

        return dutySchedules;
    }

    private String generateGender(){
        int gend = random.ints(0, 2).findFirst().getAsInt();
        String gender = gend == 0? "female" : "male";
        return gender;
    }

    private Integer generateAge(){
        return random.ints(1, 80).findFirst().getAsInt();
    }

    private List<ConnectedPersonTO> generatePassengers(){
        List<ConnectedPersonTO> passengers = new ArrayList<>();
        return passengers;
    }
}
