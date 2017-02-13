package com.mobgen.halo.android.sdk.core.management.modules;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;
import com.mobgen.halo.android.sdk.core.management.models.HaloModuleField;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the modules.
 */
public class ModulesRepository {

    /**
     * Remote data source.
     */
    private ModulesRemoteDatasource mRemoteDatasource;
    /**
     * Local data source.
     */
    private ModulesLocalDatasource mLocalDatasource;

    /**
     * Constructor for the repository.
     * @param modulesRemoteDatasource The remote data source.
     * @param modulesLocalDatasource The local data source.
     */
    public ModulesRepository(@NonNull ModulesRemoteDatasource modulesRemoteDatasource, @NonNull ModulesLocalDatasource modulesLocalDatasource) {
        AssertionUtils.notNull(modulesRemoteDatasource, "remoteDatasource");
        AssertionUtils.notNull(modulesLocalDatasource, "localDatasource");
        mRemoteDatasource = modulesRemoteDatasource;
        mLocalDatasource = modulesLocalDatasource;
    }

    /**
     * Provides the modules cached from the network.
     * @return The modules cached from the network.
     */
    public HaloResultV2<List<HaloModule>> getModulesFromNetwork(boolean withFields) {
        HaloStatus.Builder status = HaloStatus.builder();
        List<HaloModule> modules = null;
        try {
            modules = mRemoteDatasource.getModules(withFields);
            if(withFields){
                try {
                    printFieldsToLog(modules);
                } catch (HaloParsingException | JSONException jsonException) {
                    Halog.d(ModulesRepository.class, "Cannot extract MODULE FIELDS information");
                }
            }
        } catch (HaloNetException e) {
            Halog.e(getClass(), "Could not retrieve the modules from network.", e);
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), modules);
    }

    /**
     * Provides the modules.
     * @return Provides the modules from the local data source.
     */
    public HaloResultV2<Cursor> getModules() {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor cursor = null;
        try {
            mLocalDatasource.saveModules(mRemoteDatasource.getModules(false));
        } catch (HaloNetException | HaloStorageGeneralException e) {
            Halog.e(getClass(), "Error saving instances", e);
            status.error(e);
            status.dataLocal();
        } finally {
            try {
                cursor = mLocalDatasource.getModules();
            } catch (Exception e) {
                status.error(e);
            }
        }
        return new HaloResultV2<>(status.build(), cursor);
    }

    /**
     * Provides the local cached modules.
     * @return The local modules.
     */
    public HaloResultV2<Cursor> getCachedModules() {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor cursor = null;
        try {
            cursor = mLocalDatasource.getModules();
        }catch (Exception e){
            Halog.e(getClass(), "Could not obtain the modules from the local data source.", e);
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), cursor);
    }

    /**
     * Print meta data from modules to log.
     * @param modules The halo module instances.
     * @throws HaloParsingException
     * @throws JSONException
     */
    private void printFieldsToLog(List<HaloModule> modules) throws HaloParsingException, JSONException {

        StringBuilder printModuleData = new StringBuilder("\n");;
        printModuleData.append("==================== BEGIN MODULE METADATA ===================" + "\n");
        int numberOfModules = modules.size();
        for(int i=0; i<numberOfModules;i++) {
            printModuleData.append("********* BEGIN MODULE NAME: " + modules.get(i).getName() + "\n");
            printModuleData.append("MODULE ID:        " + modules.get(i).getId() + "\n");
            int numberOfFields = modules.get(i).getFields().length();
            if(numberOfFields>0){
                printModuleData.append("FIELDS DATA: "+ "\n");
                printModuleData.append("\t-------------------------------------------------------------"+ "\n");
            }
            for(int j=0; j<numberOfFields;j++) {
                HaloModuleField field = HaloModuleField.deserialize(modules.get(i).getFields().get(j).toString(), Halo.instance().framework().parser());
                printModuleData.append("\tBEGIN FIELD " + field.getName() + "\n");
                printModuleData.append("\t\tDESCRIPTION:    " + field.getDescription() + "\n");
                printModuleData.append("\t\tFIELD TYPE:     " +field.getModuleFieldType().getName() + "\n");
                int numberOfRules = field.getModuleFieldType().getRules().size();
                printModuleData.append("\t\tRULES: " + "\n");
                for(int k=0; k<numberOfRules;k++) {
                    printModuleData.append("\t\t                " +field.getModuleFieldType().getRules().get(k).getRule() + "\n");
                }
                printModuleData.append("\n");
                printModuleData.append("\tEND FIELD " + field.getName()+ "\n");
                printModuleData.append("\t--------------------------------------------------------------" + "\n");
            }
            printModuleData.append("********* END MODULE NAME: " + modules.get(i).getName() + "\n");
            printModuleData.append("==============================================================" + "\n");
        }
        printModuleData.append("=================== END MODULE METADATA   ====================" + "\n");

        int maxLogSize = 2048;
        for(int i = 0; i <= printModuleData.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > printModuleData.length() ? printModuleData.length() : end;
            Halog.d(ModulesRepository.class, "\n"  + printModuleData.substring(start, end));
        }
    }
}
