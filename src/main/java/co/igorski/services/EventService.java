package co.igorski.services;

import co.igorski.client.WebClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.TestModel;
import co.igorski.model.TestRun;
import co.igorski.model.User;
import co.igorski.model.events.Event;
import co.igorski.model.events.RunFinished;
import co.igorski.model.events.RunStarted;
import co.igorski.model.events.TestDisabled;
import co.igorski.model.events.TestFinished;
import co.igorski.model.events.TestReported;
import co.igorski.model.events.TestStarted;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.platform.engine.reporting.ReportEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class EventService {
    private static final String TEST_EVENT_ENDPOINT = "/event/test";
    private final WebClient webClient;
    private final Configuration configuration;
    private final ObjectMapper objectMapper = new ObjectMapper();

    EventService(WebClient webClient, Configuration configuration) {
        this.webClient = webClient;
        this.configuration = configuration;
    }

    TestRun testPlanStarted(Map<String, TestModel> tests, User user) throws SnitcherException {

        RunStarted runStarted = new RunStarted();
        runStarted.setUser(user);
        runStarted.setTests(new ArrayList<>(tests.values()));
        runStarted.setTimestamp(new Date());
        runStarted.setProjectName(configuration.getProjectName());

        return getTestRunResponse("/event/run/started", runStarted);
    }

    void testRunFinished(Long runId) throws SnitcherException, IOException {

        RunFinished runFinished = new RunFinished();
        runFinished.setRunId(runId);
        runFinished.setTimestamp(new Date());
        postRequest("/event/run/finished", runFinished);
    }

    private TestRun getTestRunResponse(String endpoint, Event runEvent) throws SnitcherException {
        TestRun testRunResponse;
        try {
            String response = postRequest(endpoint, runEvent);
            testRunResponse = objectMapper.readValue(response, TestRun.class);
        } catch (JsonProcessingException e) {
            throw new SnitcherException("Error when serializing Run event object to JSON", e);
        } catch (IOException e) {
            throw new SnitcherException("Error when deserializing JSON to a Run event", e);
        }
        return testRunResponse;
    }

    private String postRequest(String endpoint, Event runEvent) throws IOException, SnitcherException {
        String body = objectMapper.writeValueAsString(runEvent);
        return webClient.post(configuration.getServerUrl() + endpoint, body);
    }

    void testStarted(TestModel testModel, Long runId) throws SnitcherException {

        TestStarted testStarted = new TestStarted();
        testStarted.setTest(testModel);
        testStarted.setTimestamp(new Date());
        testStarted.setRunId(runId);
        sendPost(TEST_EVENT_ENDPOINT, testStarted);
    }

    void testFinished(TestModel testModel, Long runId) throws SnitcherException {

        TestFinished testFinished = new TestFinished();
        testFinished.setTest(testModel);
        testFinished.setTimestamp(new Date());
        testFinished.setRunId(runId);
        testFinished.setOutcome(testModel.getOutcome());
        testFinished.setError(testModel.getError());
        sendPost(TEST_EVENT_ENDPOINT, testFinished);
    }

    private void sendPost(String endpoint, Event event) throws SnitcherException {
        try {
            webClient.post(configuration.getServerUrl() + endpoint, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new SnitcherException("Error when serializing object to JSON", e);
        } catch (IOException e) {
            throw new SnitcherException("Error when sending Event request.", e);
        }
    }

    void testDisabled(TestModel testModel, Long runId) throws SnitcherException {

        TestDisabled testDisabled = new TestDisabled();
        testDisabled.setTest(testModel);
        testDisabled.setTimestamp(new Date());
        testDisabled.setRunId(runId);
        sendPost(TEST_EVENT_ENDPOINT, testDisabled);
    }

    public void testReported(Long runId, TestModel test, ReportEntry entry) throws SnitcherException {
        TestReported testReported = new TestReported();
        testReported.setTest(test);
        testReported.setTimestamp(new Date());
        testReported.setRunId(runId);
        testReported.setReportEntries(entry.getKeyValuePairs());

        sendPost(TEST_EVENT_ENDPOINT, testReported);
    }
}
