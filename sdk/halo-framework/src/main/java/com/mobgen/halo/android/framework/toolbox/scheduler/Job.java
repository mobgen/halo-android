package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The Job you want to schedule
 */
public final class Job {
    /**
     * Identity hash code.
     */
    private static final int SECRET_CODE = 0x611;
    /**
     * Network change id.
     */
    static final String STATUS_NETWORK_TYPE_KEY = "con_networktype";
    /**
     * Charging change id.
     */
    static final String STATUS_CHARGING_KEY = "con_charging";

    /**
     * Idle state change id.
     */
    static final String STATUS_IDLE_DEVICE_KEY = "con_idle";
    /**
     * No network required.
     */
    public static final int NETWORK_TYPE_NONE = 0;
    /**
     * Any network type required.
     */
    public static final int NETWORK_TYPE_ANY = 1;

    /**
     * Not metered network type.
     */
    public static final int NETWORK_TYPE_UNMETERED = 2;

    /**
     * Network type annotation definition to keep the tree type of networks.
     */
    @IntDef({NETWORK_TYPE_NONE, NETWORK_TYPE_ANY, NETWORK_TYPE_UNMETERED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NetworkType {

    }

    /**
     * The builder with the information.
     */
    private Builder mBuilder;

    /**
     * The mDeadline object to decide when this task should be executed.
     */
    Object mDeadLineObj = null; //Store mDeadline Object, list of PendingIntent(after >5min) or Runnable(otherwise)

    /**
     * The builder with the information.
     *
     * @param builder The builder.
     */
    private Job(@NonNull Builder builder) {
        mBuilder = builder;
    }

    /**
     * Creates an empty job.
     */
    private Job() {
    }

    /**
     * Creates the job from the persisted information.
     *
     * @param existInfo The info that already existed.
     * @return The job created.
     */
    @Nullable
    static Job createJobFromPersistInfo(@Nullable JobInfo existInfo) {
        if (existInfo == null) {
            return null;
        }
        Job job = new Job();
        job.mBuilder.mJobInfo = existInfo;
        try {
            Class<?> clazz = Class.forName(job.mBuilder.mJobInfo.mActionClassName);
            Object act = clazz.newInstance();
            if (act instanceof Schedule) {
                job.mBuilder.mAction = (Schedule) act;
            } else if (act instanceof ScheduleContext) {
                job.mBuilder.mAction = (ScheduleContext) act;
            }
            for (String condName : job.mBuilder.mJobInfo.mTriggers) {
                Class<?> condClz = Class.forName(condName);
                Trigger cond = (Trigger) condClz.newInstance();
                job.mBuilder.mTriggers.add(cond);
            }
            job.resetConds();
        } catch (Exception e) {
            Halog.e(Job.class, e.getMessage(), e);
            return null;
        }
        return job;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        Job other = (Job) o;
        return mBuilder.mJobInfo.equals(other.mBuilder.mJobInfo) && mBuilder.mCanBePersisted == other.mBuilder.mCanBePersisted;
    }

    /**
     * Reset the conditions.
     */
    void resetConds() {
        mBuilder.mCondSatisfied.clear();
        for (Trigger trigger : mBuilder.mTriggers) {
            mBuilder.mCondSatisfied.put(trigger.getIdentify(), false);
        }
        if (mBuilder.mJobInfo.mNetworkType != JobInfo.NETWORK_TYPE_INVALID) {
            mBuilder.needsNetwork(mBuilder.mJobInfo.mNetworkType);
        }
        if (mBuilder.mJobInfo.mNeedsCharging) {
            mBuilder.needCharging(true);
        }
        if (mBuilder.mJobInfo.mNeedsDeviceIdle) {
            mBuilder.needDeviceIdle(true);
        }
    }

    /**
     * The job information.
     *
     * @return The job info.
     */
    @Api(2.0)
    @NonNull
    public JobInfo info() {
        return mBuilder.mJobInfo;
    }

    /**
     * The action.
     *
     * @return The action.
     */
    @Api(2.0)
    @NonNull
    public Act action() {
        return mBuilder.mAction;
    }

    /**
     * The conditions satisfied.
     *
     * @return The conditions ok.
     */
    @Api(2.0)
    @NonNull
    public Map<String, Boolean> conditionsOk() {
        return mBuilder.mCondSatisfied;
    }

    /**
     * Tells if this job can be persisted.
     *
     * @return True if it can be persisted.
     */
    @Api(2.0)
    public boolean canBePersisted() {
        return mBuilder.mCanBePersisted;
    }

    /**
     * Provides the current conditions.
     *
     * @return The list of conditions.
     */
    @NonNull
    @Api(2.0)
    public List<Trigger> triggers() {
        return mBuilder.mTriggers;
    }

    /**
     * The action or context action.
     *
     * @param action The action.
     * @return The current builder.
     */
    public static Job.Builder builder(@NonNull Act action) {
        return new Builder(action);
    }

    /**
     * The job builder.
     */
    public static class Builder implements IBuilder<Job> {
        /**
         * Decides wether this can be persisted or not.
         */
        boolean mCanBePersisted;

        /**
         * Job info.
         */
        private JobInfo mJobInfo;

        /**
         * The action.
         */
        private Act mAction;

        /**
         * Conditions satisfied.
         */
        private HashMap<String, Boolean> mCondSatisfied;

        /**
         * External triggers.
         */
        private List<Trigger> mTriggers;

        /**
         * The constructor builder.
         */
        private Builder(@NonNull Act action) {
            mCanBePersisted = true;
            mCondSatisfied = new HashMap<>();
            mTriggers = new ArrayList<>();
            mJobInfo = new JobInfo();
            setAction(action);
            mJobInfo.mIdentity = generateIdentity();
        }

        /**
         * Repeat the job.
         *
         * @return The current job.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder repeat() {
            mJobInfo.mRepeat = true;
            return this;
        }

        /**
         * Repeat using a delay.
         *
         * @param delay The delay.
         * @return The current job.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder repeat(long delay) {
            if (delay <= 0) {
                throw new IllegalArgumentException("Delay must greater than 0.");
            }
            mJobInfo.mRepeat = true;
            mJobInfo.mDelay = delay;
            return this;
        }

        /**
         * The deadline when this task cannot be executed anymore.
         *
         * @param amountOfTime The amount of time.
         * @param unit         The unit.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder deadline(long amountOfTime, @NonNull TimeUnit unit) {
            long now = System.currentTimeMillis();
            mJobInfo.mDeadline = now + unit.toMillis(amountOfTime);
            return this;
        }

        /**
         * Sets the thread on which this job must be run.
         *
         * @param threadPolicy The thread policy.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder thread(@Threading.Policy int threadPolicy) {
            mJobInfo.mThreadPolicy = threadPolicy;
            return this;
        }

        /**
         * Marks the item to need the network.
         *
         * @param type The current type.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder needsNetwork(@NetworkType int type) {
            mCondSatisfied.put(STATUS_NETWORK_TYPE_KEY, StatusDevice.networkTypeSatisfied(type));
            mJobInfo.mNetworkType = type;
            return this;
        }

        /**
         * Mark this job to need the charge or not.
         *
         * @param charge True if it needs to be charged.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder needCharging(boolean charge) {
            if (charge) {
                mCondSatisfied.put(STATUS_CHARGING_KEY, StatusDevice.CHARGING_CONSTRAINT_SATISFIED.get());
            }
            mJobInfo.mNeedsCharging = charge;
            return this;
        }

        /**
         * The tag of the job.
         *
         * @param tag The tag.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder tag(@NonNull String tag) {
            mJobInfo.mTag = tag;
            return this;
        }

        /**
         * Mark this job as persisted.
         *
         * @param persist True to persist it.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder persist(boolean persist) {
            mJobInfo.mPersistAfterReboot = persist;
            return this;
        }

        /**
         * Require the device to use the idle status.
         *
         * @param idle True to use idle.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder needDeviceIdle(boolean idle) {
            if (idle) {
                mCondSatisfied.put(STATUS_IDLE_DEVICE_KEY, StatusDevice.IDLE_CONSTRAINT_SATISFIED.get());
            }
            mJobInfo.mNeedsDeviceIdle = idle;
            return this;
        }

        /**
         * Adds extra conditions.
         *
         * @param trigger The extra trigger.
         * @return The job.
         */
        @Api(2.0)
        @NonNull
        public Job.Builder trigger(@NonNull Trigger trigger) {
            AssertionUtils.notNull(trigger, "trigger");
            if (trigger.getClass().getCanonicalName() == null) {
                mCanBePersisted = false;
                if (mJobInfo.mPersistAfterReboot) {
                    throw new IllegalArgumentException("Do not use anonymous class as Trigger");
                } else {
                    Halog.w(getClass(), "Job with anonymous Trigger class can not be persist as file, so we will lose it while service restarting.");
                }
            }
            int modifier = trigger.getClass().getModifiers();
            if ((modifier & Modifier.PUBLIC) == 0 || (modifier & Modifier.STATIC) == 0) {
                mCanBePersisted = false;
                if (mJobInfo.mPersistAfterReboot) {
                    throw new IllegalArgumentException("Trigger must be PUBLIC and STATIC.");
                } else {
                    Halog.w(getClass(), "If you want this Job can be persist, please keep Trigger class as PUBLIC and STATIC. So we can recover it from service restarting.");
                }
            }
            mJobInfo.mTriggers.add(trigger.getClass().getName());
            mTriggers.add(trigger);
            mCondSatisfied.put(trigger.getIdentify(), false);
            return this;
        }

        /**
         * Generates the identity id.
         *
         * @return The mIdentity id.
         */
        private String generateIdentity() {
            String actName = (TextUtils.isEmpty(mAction.getClass().getSimpleName()) ? "Anonymous" : mAction.getClass().getSimpleName());
            return String.valueOf(SECRET_CODE) + "_" + System.identityHashCode(this) + "_" + actName;
        }

        /**
         * Sets the current action.
         *
         * @param action The action to be ser.
         */
        private void setAction(@NonNull Act action) {
            mAction = action;
            if (action.getClass().getCanonicalName() == null) {
                mCanBePersisted = false;
                if (mJobInfo.mPersistAfterReboot) {
                    throw new IllegalArgumentException("Do not use anonymous class as Action");
                } else {
                    Halog.w(getClass(), "Job with anonymous Action class can not be persist as file, so we will lose it while service restarting.");
                }
            }
            int modifier = action.getClass().getModifiers();
            if ((modifier & Modifier.PUBLIC) == 0 || (modifier & Modifier.STATIC) == 0) {
                mCanBePersisted = false;
                if (mJobInfo.mPersistAfterReboot) {
                    throw new IllegalArgumentException("Action must be PUBLIC and STATIC.");
                }
            }
            mJobInfo.mActionClassName = action.getClass().getName();
        }

        @Api(2.0)
        @NonNull
        @Override
        public Job build() {
            if (mTriggers.isEmpty() && mJobInfo.mRepeat && mJobInfo.mDelay < 60 * 60 * 1000/*1 hour*/) {
                throw new IllegalArgumentException("Your job may be triggered too often, please keep the delay above 1 hour.");
            }
            return new Job(this);
        }
    }

    /**
     * The job information that will be run and persisted into the
     * service.
     */
    static class JobInfo {
        /**
         * Default type name.
         */
        private static final String DEFAULT_TAG = "Job";
        /**
         * Invalid network type.
         */
        static final int NETWORK_TYPE_INVALID = -1;

        /**
         * Persist this job after rebooting.
         */
        boolean mPersistAfterReboot;
        /**
         * The thread in which this job must run.
         */
        @Threading.Policy
        int mThreadPolicy;
        /**
         * The network type.
         */
        int mNetworkType;
        /**
         * Needs charging trigger.
         */
        boolean mNeedsCharging;
        /**
         * Needs the device to be idle
         */
        boolean mNeedsDeviceIdle;
        /**
         * The identifying string.
         */
        String mIdentity;
        /**
         * This task must be repeated.
         */
        boolean mRepeat;
        /**
         * The delay to execute the task.
         */
        long mDelay;
        /**
         * The deadline of the task.
         */
        long mDeadline;
        /**
         * The happenings.
         */
        long mHappen;
        /**
         * The current conditions.
         */
        List<String> mTriggers;
        /**
         * The action classname.
         */
        String mActionClassName;
        /**
         * The tag.
         */
        String mTag;

        /**
         * The constructor for the job.
         */
        private JobInfo() {
            mTag = DEFAULT_TAG;
            mNetworkType = NETWORK_TYPE_INVALID;
            mThreadPolicy = Threading.POOL_QUEUE_POLICY;
            mPersistAfterReboot = false;
            mNeedsDeviceIdle = false;
            mNeedsCharging = false;
            mRepeat = false;
            mDelay = -1L;
            mDeadline = -1L;
            mHappen = -1L;
            mTriggers = new ArrayList<>();
        }

        /**
         * Reads the job info from a file.
         *
         * @param file The file.
         * @return The job info.
         * @throws IOException Error reading te file.
         */
        public static JobInfo readFromFile(@NonNull File file) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder ret = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                ret.append(line).append("\n");
            }
            return fromJson(ret.toString());
        }


        /**
         * Creates the job info given a json.
         *
         * @param data The data.
         * @return The job info created.
         * @throws IOException Error while parsing the job info.
         */
        @NonNull
        @SuppressWarnings("all")
        private static JobInfo fromJson(@NonNull String data) throws IOException {
            JobInfo info = new JobInfo();
            try {
                JSONObject json = new JSONObject(data);
                info.mPersistAfterReboot = json.optBoolean("persist", false);
                info.mThreadPolicy = json.optInt("thread", Threading.POOL_QUEUE_POLICY);
                info.mNetworkType = json.optInt("network", NETWORK_TYPE_INVALID);
                info.mNeedsCharging = json.optBoolean("charging", false);
                info.mNeedsDeviceIdle = json.optBoolean("idle", false);
                JSONArray triggers = json.optJSONArray("triggers");
                for (int i = 0; i < triggers.length(); i++) {
                    String jsonTrigger = triggers.optString(i);
                    info.mTriggers.add(jsonTrigger);
                }
                info.mActionClassName = json.getString("actname");
                info.mTag = json.optString("tag", DEFAULT_TAG);
            } catch (JSONException e) {
                throw new IOException("Error parsing the data", e);
            }
            return info;
        }

        /**
         * Creates the json from the job info.
         *
         * @param info The info.
         * @return The json created as string.
         */
        @NonNull
        public String toJson(@NonNull JobInfo info) throws IOException {
            JSONObject json = new JSONObject();
            try {
                json.put("persist", info.mPersistAfterReboot);
                json.put("thread", info.mThreadPolicy);
                json.put("network", info.mNetworkType);
                json.put("charging", info.mNeedsCharging);
                json.put("idle", info.mNeedsDeviceIdle);
                JSONArray triggersArray = new JSONArray();
                for (String trigger : mTriggers) {
                    triggersArray.put(trigger);
                }
                json.put("triggers", triggersArray);
                json.put("actname", info.mActionClassName);
                json.put("tag", info.mTag);
            } catch (JSONException e) {
                throw new IOException("Error while creating the job json object.", e);
            }
            return json.toString();
        }

        /**
         * Writes the job info into the directory.
         *
         * @param dir The directory.
         * @throws IOException Error while writting the job.
         */
        public void writeToFile(File dir) throws IOException {
            File file = new File(dir, mIdentity + ".job");
            //do this if this method is only called because of device shutdown
            mHappen = -1L;
            String content = toJson(this);
            Writer writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        }

        /**
         * Try deleting the file directory.
         *
         * @param dir The directory.
         */
        public void tryDelete(File dir) {
            File file = new File(dir, mIdentity + ".job");
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof JobInfo))
                return false;
            JobInfo other = (JobInfo) o;
            //drop happen and identify
            if (this.mPersistAfterReboot != other.mPersistAfterReboot) return false;
            if (this.mThreadPolicy != other.mThreadPolicy) return false;
            if (this.mNetworkType != other.mNetworkType) return false;
            if (this.mNeedsCharging != other.mNeedsCharging) return false;
            if (this.mNeedsDeviceIdle != other.mNeedsDeviceIdle) return false;
            if (this.mRepeat != other.mRepeat) return false;
            if (this.mDelay != other.mDelay) return false;
            if (this.mDeadline != other.mDeadline) return false;
            if (!this.mTriggers.equals(other.mTriggers)) return false;
            if (!this.mActionClassName.equals(other.mActionClassName)) return false;
            if (!this.mTag.equals(other.mTag)) return false;
            return true;
        }
    }
}
