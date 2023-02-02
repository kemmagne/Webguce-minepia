/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  lissouck
 * Created: 29 janv. 2023
 */

ALTER TABLE WEBGUCE.VT2_MINEPDED_REGISTRATION
    ADD (VT_MINEPDED_FEES_AMOUNT NUMBER(38,0));

ALTER TABLE WEBGUCE.VT2_MINEPDED_REGISTRATION
    ADD (VT_MINEPDED_TOTAL_FEES_AMOUNT NUMBER(38,0));