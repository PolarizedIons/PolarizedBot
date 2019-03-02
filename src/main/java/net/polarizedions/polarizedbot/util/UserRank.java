package net.polarizedions.polarizedbot.util;

public enum UserRank {

    GLOBAL_ADMIN(100),
    LOCAL_ADMIN(50),
    DEFAULT(10);

    public int rank;

    UserRank(int rank) {
        this.rank = rank;
    }
}
