package com.liyang.app.api;

import java.io.IOException;

public interface CurdApi {
    public long countByGeo(String geo);

    public void dealDomainList(String txtPath) throws IOException;
}
