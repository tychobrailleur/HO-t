package core.file.xml;

import core.model.arena.*;
import core.util.HODateTime;
import core.util.ResourceUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class XMLArenaParserTest {

    private static final String FILENAME = "arenadetails.xml";
    private static final String VERSION = "1.3";

    @Test
    void parseArenaFromString_noExpansion() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString("arenaDetails_noExpansion.xml");

        final var expected = Pair.of(
                new HattrickDataInfo(FILENAME,
                        VERSION,
                        1234567,
                        HODateTime.fromHT("2024-08-21 01:13:12")),
                new Arena(2345678,
                        "ArenaName",
                        new TeamIdName(3456789, "TeamName"),
                        new LeagueIdName(3, "LeagueName"),
                        new RegionIdName(227, "RegionName"),
                        new Capacity(8000,
                                3000,
                                1792,
                                282,
                                13074,
                                HODateTime.fromHT("2024-08-13 00:05:26"), null),
                        Optional.empty()));

        // when
        final var result = XMLArenaParser.parseArenaFromString(content);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void parseArenaFromString_withExpansion() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString("arenaDetails_withExpansion.xml");

        final var expected = Pair.of(
                new HattrickDataInfo(FILENAME,
                        VERSION,
                        1234567,
                        HODateTime.fromHT("2024-08-21 11:08:22")),
                new Arena(
                        2345678,
                        "ArenaName",
                        new TeamIdName(3456789, "TeamName"),
                        new LeagueIdName(3, "LeagueName"),
                        new RegionIdName(227, "RegionName"),
                        new Capacity(8000,
                                3000,
                                1792,
                                282,
                                13074,
                                null, null),
                        Optional.of(new Capacity(226,
                                234,
                                1138,
                                78,
                                1676,
                                null,
                                HODateTime.fromHT("2024-08-30 11:07:17")))));
        // when
        final var result = XMLArenaParser.parseArenaFromString(content);

        // then
        assertThat(result).isEqualTo(expected);
    }
}