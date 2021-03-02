package ru.list.surkovr.services;

import javax.naming.ServiceUnavailableException;

public interface ProcessFastFixMessageService {

    void processFastMessage(byte[] fastEncodedMessage) throws ServiceUnavailableException;
}
