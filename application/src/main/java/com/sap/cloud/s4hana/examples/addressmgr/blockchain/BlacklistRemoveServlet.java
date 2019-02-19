package com.sap.cloud.s4hana.examples.addressmgr.blockchain;

import com.google.common.base.Optional;
import com.sap.cloud.s4hana.examples.addressmgr.blockchain.commands.RemoveEmailBlacklistEntryCommand;
import com.sap.cloud.s4hana.examples.addressmgr.util.CloudPlatformService;
import com.sap.cloud.s4hana.examples.addressmgr.util.HttpServlet;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.services.scp.blockchain.hyperledgerfabric.FabricService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/blacklistremove")
public class BlacklistRemoveServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = CloudLoggerFactory.getLogger(BlacklistRemoveServlet.class);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String email = request.getParameter("email");
        if (StringUtils.isBlank(email)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter 'email'");
        } else {
            try {
                final BlacklistService blacklistService = new BlacklistService(new CloudPlatformService(),
                        FabricService.create());
                final Optional<Boolean> result = new RemoveEmailBlacklistEntryCommand(blacklistService, email.trim())
                        .execute();

                response.setContentType("text/plain");
                if (!result.isPresent()) {
                    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                }

            } catch (Exception e) {
                logger.error("Failed to remove email from blacklist", e);
                response.reset();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to remove business partner email from blacklist: " + e.getMessage());
            }
        }
    }
}
