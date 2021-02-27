package ru.list.surkovr;

import javax.naming.ServiceUnavailableException;

public interface ProcessFastFixMessageService {

    void processFastMessage(byte[] fastEncodedMessage) throws ServiceUnavailableException;
}
