package com.newgen.worksteps;

import com.kelmorgan.ibpsformapis.apis.FormApi;
import com.newgen.iforms.EControl;
import com.newgen.iforms.FormDef;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.custom.IFormServerEventHandler;
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.util.*;
import com.newgen.util.mail.MailMessage;
import com.newgen.util.mail.MailSetup;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Aco implements IFormServerEventHandler, Constants, SharedI {
    private final Logger logger = LogGenerator.getLoggerInstance(Aco.class);

    @Override
    public void beforeFormLoad(FormDef formDef, IFormReference ifr) {
        formLoad(ifr);
    }

    @Override
    public String setMaskedValue(String s, String s1) {
        return s1;
    }

    @Override
    public JSONArray executeEvent(FormDef formDef, IFormReference iFormReference, String s, String s1) {
        return null;
    }

    @Override
    public String executeServerEvent(IFormReference ifr, String control, String event, String data) {
        switch (event) {
            case onLoadEvent:
                break;
            case onChangeEvent:
                break;
            case onClickEvent:
                break;
            case onDoneEvent: {
                switch (control) {
                    case decisionHistoryEvent: {
                        Shared.setDecisionHistory(ifr);
                        break;
                    }
                    case sendMailEvent:{
                        sendMail(ifr);
                    }
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public JSONArray validateSubmittedForm(FormDef formDef, IFormReference iFormReference, String s) {
        return null;
    }

    @Override
    public String executeCustomService(FormDef formDef, IFormReference iFormReference, String s, String s1, String s2) {
        return null;
    }

    @Override
    public String getCustomFilterXML(FormDef formDef, IFormReference iFormReference, String s) {
        return null;
    }

    @Override
    public String generateHTML(EControl eControl) {
        return null;
    }

    @Override
    public String introduceWorkItemInWorkFlow(IFormReference iFormReference, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public String introduceWorkItemInWorkFlow(IFormReference iFormReference, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, WorkdeskModel workdeskModel) {
        return null;
    }

    @Override
    public void formLoad(IFormReference ifr) {
        try {
            Shared.hideSections(ifr);
            FormApi.clearFields(ifr, new String[]{remarksLocal, decisionHistoryFlagLocal});
            FormApi.setVisible(ifr, new String[]{accountListSection, pepInfoSection, pepCategorySection, pepVerificationSection, decisionSection});
            Shared.checkPepVerification(ifr);
            Shared.checkPepStatus(ifr);
            FormApi.enableFields(ifr, new String[]{decisionLocal, remarksLocal});
            FormApi.setMandatory(ifr, new String[]{decisionLocal, remarksLocal});
            setDecision(ifr);
        } catch (Exception e) {
            logger.error("Exception occurred in Aco Initiator FormLoad : " + e.getMessage());
        }
    }

    @Override
    public void sendMail(IFormReference ifr) {
        MailMessage mailMessage = new MailMessage(ifr);
        String message;
        String sendTo = Shared.getUsersMailsInGroup(ifr, LoadProp.pepMailGroup);
        if (Shared.isDecisionApprove(ifr)) {
           // sendTo = Shared.getUsersMailsInGroup(ifr, amlGroupName);
            message = mailMessage.getApproveMsg();
            new MailSetup(ifr, FormApi.getWorkItemNumber(ifr), sendTo, empty, LoadProp.mailSubject, message);
        } else if (Shared.isDecisionReturn(ifr)) {
          //  sendTo = Shared.getUsersMailsInGroup(ifr, rmGroupLabel + Shared.getUserSol(ifr));
            message = mailMessage.getRejectMsg();
            new MailSetup(ifr, FormApi.getWorkItemNumber(ifr), sendTo, empty, LoadProp.mailSubject, message);
        }
    }


    @Override
    public void setDecision(IFormReference ifr) {
        Shared.setDecision(ifr, decisionLocal, new String[]{decApprove, decReturn});

    }
}
