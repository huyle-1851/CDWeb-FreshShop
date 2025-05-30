//package vn.edu.hcmuaf.freshshop.modelmapping;
//
//import vn.edu.hcmuaf.freshshop.model.Role;
//import vn.edu.hcmuaf.freshshop.model.User;
//import vn.edu.hcmuaf.freshshop.model.UserRole;
//import vn.edu.hcmuaf.freshshop.request.RegisterRequest;
//
//import java.util.UUID;
//
//public class UserMapping {
//    public static User mapToUser(RegisterRequest request, String passwordHash) {
//        User user = new User();
//        UUID id = UUID.randomUUID();
//        user.setId(id);
//        user.setFirstname(request.getFirstName());
//        user.setLastname(request.getLastName());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordHash);
//        user.setAddress(request.getAddress());
//        user.setPhone(request.getPhoneNumber());
//        return user;
//    }
//
//    public static UserRole mapToRole(User user, Role role) {
//        UUID id = UUID.randomUUID();
//        UserRole roleCreate = new UserRole();
//        roleCreate.setId(id);
//        roleCreate.setRole(role);
//        roleCreate.setUser(user);
//        return roleCreate;
//    }
//}
