/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guce.process.atm.repositories.impl;

/**
 *
 * @author NGC
 */
public interface CoreProcessingFacadeATMLocal {
    public Number countCoreProcessingStartWithRecordIdAndProcessingType(String recordID, String type);
}
