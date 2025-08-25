package StudentWallet.StudentWallet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import StudentWallet.StudentWallet.security.JwtService;
import StudentWallet.StudentWallet.security.MyUserDetailService;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailService userDetailService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && accessor.getCommand() != null && accessor.getCommand().toString().equals("CONNECT")) {
            String token = null;
            if (accessor.getNativeHeader("Authorization") != null) {
                token = accessor.getNativeHeader("Authorization").get(0);
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
            }
            if (token != null) {
                String username = jwtService.extractUsername(token);
                if (username != null) {
                    UserDetails userDetails = userDetailService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token)) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        return message;
    }
}
