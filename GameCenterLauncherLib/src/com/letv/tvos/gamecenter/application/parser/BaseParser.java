package com.letv.tvos.gamecenter.application.parser;

import com.letv.tvos.gamecenter.application.network.IResponse;

public interface BaseParser<T extends IResponse> {
	
	public T parse(String paramString); 

}
