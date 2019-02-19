package com.sap.cloud.s4hana.examples.addressmgr;

import com.google.gson.Gson;
import com.sap.cloud.s4hana.examples.addressmgr.custom.namespaces.bupasocialmedia.SocialMediaAccount;
import com.sap.cloud.s4hana.examples.addressmgr.sfsf.namespaces.timeaccount.TimeAccount;
import com.sap.cloud.s4hana.examples.addressmgr.sfsf.services.DefaultTimeAccountService;
import com.sap.cloud.s4hana.examples.addressmgr.util.HttpServlet;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.ErpConfigContext;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/social-media-accounts")
public class SocialMediaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = CloudLoggerFactory.getLogger(SocialMediaServlet.class);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String id = request.getParameter("id");

        if (!BusinessPartnerServlet.validateInput(id)) {
            logger.warn("Invalid request to retrieve time account balances of a business partner, id: {}.", id);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    String.format("Invalid business partner ID '%s'. " +
                                    "Business partner ID must not be empty or longer than 10 characters.",
                            id));
            return;
        }

        logger.info("Retrieving time account balances of business partner with id {}", id);

        try {
            final List<TimeAccount> timeAccounts = new DefaultTimeAccountService()
                    .getAllTimeAccount()
                    .filter(TimeAccount.USER_ID.eq(getMappedId(id))
                            .and(TimeAccount.BOOKING_END_DATE.lt(LocalDateTime.now())))
                    .top(10)
                    .execute(new ErpConfigContext("SFSF"));

            response.setContentType("application/json");
            response.getWriter().write(new Gson().toJson(convert(timeAccounts)));
        } catch (ODataException e) {
            logger.error("Error while retrieving time accounts");
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private List<SocialMediaAccount> convert(final List<TimeAccount> timeAccounts) {
        return timeAccounts.stream().map(account ->
            SocialMediaAccount.builder().account(String.valueOf(
                    Duration.between(account.getBookingStartDate(), account.getBookingEndDate()).toHours()))
                    .service(getMappedAccountType(account.getAccountType())).build()
        ).collect(Collectors.toList());
    }

    private String getMappedId(String id) {
        switch(id) {
            case "1003764": return "80300";
            case "1003765": return "103008";
            case "1003766": return "102028";
            case "1003767": return "103009";
            default: return  "102027";
        }
    }

    private String getMappedAccountType(String type) {
        switch(type) {
            case "TAT_VAC_REC": return "Vacation";
            case "TAT_SICK_REC": return "Sickness";
            default: return "Other";
        }
    }
}
