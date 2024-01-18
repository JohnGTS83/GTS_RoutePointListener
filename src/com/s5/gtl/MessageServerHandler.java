package com.s5.gtl;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.s5.common.Loger;
import com.s5.dao.TrackerDetailDAO;


public class MessageServerHandler extends IoHandlerAdapter {
	
	ExecutorService service = Executors.newFixedThreadPool(30);
	private static final CharsetDecoder CHARSET = Charset.forName("UTF-8").newDecoder();
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		Loger.log(Loger.ERROR,"MessageServerHandler.exceptionCaught");
		cause.printStackTrace();
		session.closeNow();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		try {
			IoBuffer messageIO = (IoBuffer)message;
			String data = messageIO.getString(CHARSET);
			if(StringUtils.isNotEmpty(data)) {
				service.submit(new GMSDispatcher(data));
			} else {
				TrackerDetailDAO.getInstance().updateList();
			}
			messageIO.clear();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}