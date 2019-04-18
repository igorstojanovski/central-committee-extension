package co.igorski.model.events;

import co.igorski.model.TestModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TestReported extends Event {
    private Long runId;
    private TestModel test;
    private Map<String, String> reportEntries;
}
