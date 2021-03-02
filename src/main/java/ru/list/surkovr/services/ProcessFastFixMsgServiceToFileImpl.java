package ru.list.surkovr.services;

import javax.naming.ServiceUnavailableException;

public class ProcessFastFixMsgServiceToFileImpl implements ProcessFastFixMessageService {
    @Override
    public void processFastMessage(byte[] fastEncodedMessage) throws ServiceUnavailableException {
        // TODO write to file...
    }
}
