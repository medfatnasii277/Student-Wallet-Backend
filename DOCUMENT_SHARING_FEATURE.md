# Document Sharing Feature Implementation

## Overview
This document describes the implementation of a document sharing feature for the StudentWallet application. The feature allows students to share documents with other students without duplicating files in storage, while maintaining proper access control and providing real-time notifications.

## Key Features

### 1. Document Sharing Without Duplication
- **No file duplication**: Files are never copied when shared
- **Permission-based access**: Access is controlled through database relationships
- **Multiple recipients**: Same document can be shared with multiple students

### 2. Access Control
- **Owner-only operations**: Only document owners can delete or modify files
- **Permission validation**: Backend enforces access control for all operations
- **Secure sharing**: Students can only access documents they own or have been explicitly shared with

### 3. Real-time Notifications
- **WebSocket integration**: Instant notifications via WebSocket connections
- **Notification types**: Document shared, access revoked, system messages
- **User-specific delivery**: Notifications are sent directly to specific users

## Architecture

### New Entities

#### DocumentShare
- Manages sharing relationships between documents and recipients
- Includes optional message when sharing
- Tracks sharing timestamp
- Prevents duplicate shares (unique constraint on document + recipient)

#### Notification
- Stores all notifications for users
- Supports different notification types
- Tracks read/unread status
- Includes metadata (sender, document, timestamp)

### New Services

#### DocumentShareService
- Handles document sharing business logic
- Manages access control
- Integrates with notification system
- Provides methods for viewing shared documents

#### NotificationService
- Manages notification creation and delivery
- Handles WebSocket messaging
- Provides notification management (mark as read, etc.)

### New Controllers

#### DocumentShareController
- **POST** `/documents/{documentId}/share` - Share document with student
- **GET** `/documents/accessible` - Get all accessible documents (owned + shared)
- **GET** `/documents/{documentId}/recipients` - View document recipients
- **DELETE** `/documents/{documentId}/share` - Revoke access
- **GET** `/documents/shared-by-me` - View documents shared by current user
- **GET** `/documents/shared-with-me` - View documents shared with current user

#### NotificationController
- **GET** `/notifications` - Get all notifications
- **GET** `/notifications/unread` - Get unread notifications
- **GET** `/notifications/unread/count` - Get unread count
- **POST** `/notifications/{id}/read` - Mark notification as read
- **POST** `/notifications/read-all` - Mark all as read

### WebSocket Configuration
- **Endpoint**: `/ws` (with SockJS fallback)
- **User-specific messages**: `/user/{username}/queue/notifications`
- **Broadcast messages**: `/topic/notifications`

## API Usage Examples

### Sharing a Document
```bash
POST /documents/123/share
{
  "recipientEmail": "student@example.com",
  "message": "Here's the study material for our project"
}
```

### Getting Accessible Documents
```bash
GET /documents/accessible
```
Returns both owned and shared documents with ownership information.

### Revoking Access
```bash
DELETE /documents/123/share
{
  "recipientEmail": "student@example.com"
}
```

## Database Schema Changes

### New Tables
1. **document_shares** - Document sharing relationships
2. **notifications** - User notifications

### Updated Tables
1. **documents** - No changes (maintains existing structure)
2. **students** - No changes (email field already exists)

## Security Features

### Access Control
- Document owners can only share their own documents
- Recipients can only access shared documents
- All operations validate user permissions

### File Security
- File paths are normalized to prevent directory traversal
- Access control is enforced at the service layer
- No direct file access without permission checks

## Real-time Notifications

### WebSocket Connection
Clients connect to `/ws` endpoint and subscribe to user-specific notification queues.

### Notification Types
1. **DOCUMENT_SHARED** - When a document is shared
2. **DOCUMENT_ACCESSED** - When a shared document is accessed
3. **SYSTEM_MESSAGE** - General system notifications

### Client Integration
```javascript
// Connect to WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Subscribe to notifications
stompClient.subscribe('/user/queue/notifications', function(notification) {
    const data = JSON.parse(notification.body);
    // Handle notification
    showNotification(data);
});
```

## Testing the Feature

### 1. Start the Application
```bash
./mvnw spring-boot:run
```

### 2. Test Document Sharing
- Upload a document
- Share it with another student's email
- Verify the recipient receives a real-time notification
- Verify the recipient can access the shared document

### 3. Test Access Control
- Try to access a document without permission
- Verify access is denied
- Try to share someone else's document
- Verify sharing is denied

### 4. Test Notifications
- Check notification endpoints
- Verify WebSocket connections
- Test real-time delivery

## Future Enhancements

### Potential Improvements
1. **Bulk sharing**: Share multiple documents at once
2. **Sharing groups**: Create groups for easier sharing
3. **Expiring shares**: Set expiration dates for shared documents
4. **Audit logging**: Track all access and sharing activities
5. **Email notifications**: Fallback to email for offline users

### Performance Considerations
1. **Pagination**: Implement pagination for large document lists
2. **Caching**: Cache frequently accessed document metadata
3. **Async processing**: Process notifications asynchronously
4. **Database indexing**: Optimize queries with proper indexes

## Troubleshooting

### Common Issues
1. **WebSocket connection failures**: Check CORS configuration
2. **Permission denied errors**: Verify user authentication and ownership
3. **Notification delivery issues**: Check WebSocket configuration
4. **Database constraint violations**: Ensure unique sharing relationships

### Debug Information
- Enable SQL logging in `application.properties`
- Check WebSocket connection status
- Monitor notification delivery
- Verify database relationships

## Conclusion

The document sharing feature provides a secure, efficient way for students to collaborate while maintaining data integrity and access control. The real-time notification system ensures users are immediately informed of relevant events, enhancing the user experience and collaboration capabilities of the StudentWallet application. 