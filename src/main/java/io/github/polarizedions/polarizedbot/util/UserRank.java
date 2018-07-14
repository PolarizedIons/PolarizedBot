package io.github.polarizedions.polarizedbot.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum UserRank {
    BOT_OWNER(100, ""),
    GUILD_ADMIN(50, "admin"),
    DEFAULT(10, "none"),
    IGNORED(-9999, "ignored");

    public int rank;
    public String rankName;
    UserRank(int rank, String rankName) {
        this.rank = rank;
        this.rankName = rankName;
    }

    @Nullable
    public static UserRank getByName(String rankName) {
        for (UserRank rank : UserRank.values()) {
            if (rank.rankName.equals(rankName)) {
                return rank;
            }
        }

        return null;
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (UserRank rank : UserRank.values()) {
            if (!rank.rankName.isEmpty()) {
                names.add(rank.rankName);
            }
        }

        return names;
    }
}
