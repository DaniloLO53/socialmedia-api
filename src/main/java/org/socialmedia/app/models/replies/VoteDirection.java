package org.socialmedia.app.models.replies;

import lombok.Getter;

@Getter
public enum VoteDirection {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int value;

    VoteDirection(int value) {
        this.value = value;
    }
}
