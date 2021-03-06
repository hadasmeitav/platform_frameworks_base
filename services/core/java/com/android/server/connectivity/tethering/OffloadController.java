/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.connectivity.tethering;

import android.net.LinkProperties;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

/**
 * A class to encapsulate the business logic of programming the tethering
 * hardware offload interface.
 *
 * @hide
 */
public class OffloadController {
    private static final String TAG = OffloadController.class.getSimpleName();

    private final Handler mHandler;
    private final OffloadHardwareInterface mHwInterface;
    private boolean mConfigInitialized;
    private boolean mControlInitialized;
    private LinkProperties mUpstreamLinkProperties;

    public OffloadController(Handler h, OffloadHardwareInterface hwi) {
        mHandler = h;
        mHwInterface = hwi;
    }

    public void start() {
        if (started()) return;

        if (!mConfigInitialized) {
            mConfigInitialized = mHwInterface.initOffloadConfig();
            if (!mConfigInitialized) {
                Log.d(TAG, "tethering offload config not supported");
                return;
            }
        }

        // TODO: Create and register ITetheringOffloadCallback.
        mControlInitialized = mHwInterface.initOffloadControl();
    }

    public void stop() {
        mUpstreamLinkProperties = null;
        mHwInterface.stopOffloadControl();
        mControlInitialized = false;
        mConfigInitialized = false;
    }

    public void setUpstreamLinkProperties(LinkProperties lp) {
        if (!started()) return;

        // TODO: setUpstreamParameters().
        mUpstreamLinkProperties = lp;
    }

    // TODO: public void addDownStream(...)

    private boolean started() {
        return mConfigInitialized && mControlInitialized;
    }
}
