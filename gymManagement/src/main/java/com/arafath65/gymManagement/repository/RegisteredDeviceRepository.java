package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.RegisteredDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredDeviceRepository
        extends JpaRepository<RegisteredDevice, Integer> {

    Optional<RegisteredDevice> findByDeviceUuid(
            String deviceUuid);

    Optional<RegisteredDevice> findByDeviceToken(
            String deviceToken);

    Optional<RegisteredDevice> findByDeviceUuidAndDeviceToken(
            String deviceUuid,
            String deviceToken);

    boolean existsByDeviceUuid(
            String deviceUuid);

    boolean existsByDeviceToken(
            String deviceToken);
}

//public interface RegisteredDeviceRepository
//        extends JpaRepository<RegisteredDevice, Integer> {
//
//    Optional<RegisteredDevice> findByDeviceUuid(String deviceUuid);
//
//    Optional<RegisteredDevice> findByDeviceToken(String deviceToken);
//
//    boolean existsByDeviceUuid(String deviceUuid);
//
//    boolean existsByDeviceToken(String deviceToken);
//
//    Optional<RegisteredDevice> findByDeviceUuidAndDeviceToken(
//            String deviceUuid,
//            String deviceToken
//    );
//}