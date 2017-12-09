package org.capy.musicbot.service.converters;

public interface EntryConverter<From, To> {

    To convert(From entry);

    To join(To entry, From source);

}
