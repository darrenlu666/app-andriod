/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011, 2012, FrostWire(TM). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.codyy.erpsportal.commons.services;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.codyy.erpsportal.commons.exception.CoreRuntimeException;
import com.codyy.erpsportal.commons.utils.ThreadPool;

import java.io.File;

public final class Engine implements IEngineService {

	private IEngineService service;
	private static Engine instance;

	public synchronized static void create(Application context) {
		if (instance != null) {
			return;
		}
		instance = new Engine(context);
	}

	public static Engine instance() {
		if (instance == null) {
			throw new CoreRuntimeException("Engine not created");
		}
		return instance;
	}

	private Engine(Application context) {
		startEngineService(context); // 启动SngineService
	}

	/**
	 * 
	 * @param context
	 *            This must be the application context, otherwise there will be
	 *            a leak.
	 */
	private void startEngineService(final Context context) {
		Intent i = new Intent();
		i.setClass(context, EngineService.class);
		context.startService(i);
		context.bindService(i, new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Engine.this.service = ((EngineService.EngineServiceBinder) service).getService();
			}
		}, Context.BIND_AUTO_CREATE);
	}

	@Override
	public byte getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStarting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startServices() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopServices(boolean disconnected) {
		// TODO Auto-generated method stub

	}

	@Override
	public ThreadPool getThreadPool() {
		return service != null ? service.getThreadPool() : null;
	}

	@Override
	public void notifyDownloadFinished(String displayName, File file) {
		if (service != null) {
            service.notifyDownloadFinished(displayName, file);
        }
	}

	@Override
	public boolean moveTempFile(File savePath) {
		return service != null && service.moveTempFile(savePath);
	}

	@Override
	public boolean renameFile(File tempPath, String filename) {
		return service != null ? service.renameFile(tempPath, filename) : false;
	}

}
