package nu.hjemme.persistence;

import nu.hjemme.client.domain.GuestBook;

public interface GuestBookEntity extends GuestBook {
    void setTitle(String title);

    void setUser(UserEntity userEntity);
}