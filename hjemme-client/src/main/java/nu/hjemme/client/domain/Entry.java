package nu.hjemme.client.domain;

import nu.hjemme.client.datatype.Name;

import java.time.LocalDateTime;

public interface Entry {

    /** @return creation time of an entry */
    LocalDateTime getCreatedTime();

    /** @return the actual entry */
    String getEntry();

    /** @return the creator which is the originator of the entry */
    Name getCreatorName();
}