package dev.puanputri.portfolio.contact;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between {@link ContactRequest}, {@link Contact}, and {@link ContactResponse}.
 * CDI component model wires this into Quarkus's bean container automatically.
 */
@Mapper(componentModel = "cdi")
public interface ContactMapper {

    /**
     * Maps a validated inbound request DTO to a new {@link Contact} entity.
     * {@code id} and {@code createdAt} are intentionally excluded — they are
     * assigned by the database and the {@code @PrePersist} hook respectively.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    Contact toEntity(ContactRequest request);

    /**
     * Maps a persisted {@link Contact} entity to an outbound response record.
     */
    ContactResponse toResponse(Contact contact);
}
