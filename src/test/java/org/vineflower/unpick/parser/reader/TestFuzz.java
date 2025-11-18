package org.vineflower.unpick.parser.reader;

import java.io.IOException;
import java.io.StringReader;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import org.vineflower.unpick.parser.UnpickParseException;
import org.vineflower.unpick.parser.UnpickV3Reader;
import org.vineflower.unpick.parser.tree.UnpickV3Visitor;

public final class TestFuzz {
    @EnabledIfSystemProperty(named = "unpick.fuzzReader", matches = "true")
    @FuzzTest
    public void fuzzReader(FuzzedDataProvider data) throws IOException {
        String unpickFile = data.consumeRemainingAsString();
        try (UnpickV3Reader reader = new UnpickV3Reader(new StringReader(unpickFile))) {
            reader.accept(new UnpickV3Visitor() {
            });
        } catch (UnpickParseException ignore) {
            // we're trying to catch crashes other than syntax errors
        }
    }
}
