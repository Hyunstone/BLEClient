package com.lilly.ble.util

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ContentValues.TAG
import android.util.Log
import com.lilly.ble.CHARACTERISTIC_COMMAND_STRING
import com.lilly.ble.CHARACTERISTIC_RESPONSE_STRING
import com.lilly.ble.SERVICE_STRING
import java.util.*
import kotlin.collections.ArrayList

class BluetoothUtils {
    companion object {
        /**
         * Find characteristics of BLE
         * @param gatt gatt instance
         * @return list of found gatt characteristics
         */
        fun findBLECharacteristics(gatt: BluetoothGatt): List<BluetoothGattCharacteristic> {
            Log.d(TAG, "엥")
            val matchingCharacteristics: MutableList<BluetoothGattCharacteristic> = ArrayList()
            Log.d(TAG, "띠용")
            val serviceList = gatt.services
            Log.d(TAG, "씨발")
            val service = findGattService(serviceList) ?: return matchingCharacteristics
            Log.d(TAG, "씨바알")
            val characteristicList = service.characteristics
            for (characteristic in characteristicList) {
                Log.d(TAG, "characteristic: $characteristic !!")

                // 여기서 매칭이 안뜸
                // 여기 파보자
                if (isMatchingCharacteristic(characteristic)) {
                    Log.d(TAG, "cha 찾아보기: ${characteristic.value}")
                    matchingCharacteristics.add(characteristic)
                }
            }
            return matchingCharacteristics
        }

        /**
         * Find command characteristic of the peripheral device
         * @param gatt gatt instance
         * @return found characteristic
         */
        fun findCommandCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
            return findCharacteristic(gatt, CHARACTERISTIC_COMMAND_STRING)
        }

        /**
         * Find response characteristic of the peripheral device
         * @param gatt gatt instance
         * @return found characteristic
         */
        fun findResponseCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
//            Log.d(TAG, "이건")
//            findBLECharacteristics(gatt);
//            Log.d(TAG, "되나?")

            return findCharacteristic(gatt, CHARACTERISTIC_RESPONSE_STRING)
        }

        /**
         * Find the given uuid characteristic
         * @param gatt gatt instance
         * @param uuidString uuid to query as string
         */
        private fun findCharacteristic(
            gatt: BluetoothGatt,
            uuidString: String
        ): BluetoothGattCharacteristic? {
            val serviceList = gatt.services
            Log.d(TAG, "여기1?")
            // 여기서 오류 터짐
            val service = findGattService(serviceList) ?: return null
            Log.d(TAG, "여기2?")
            val characteristicList = service.characteristics
            for (characteristic in characteristicList) {
                Log.d(TAG, "!!!" + characteristic.toString())
                // 여기서 알맞는 characteristic을 못 골라오는듯 함
                if (matchCharacteristic(characteristic, uuidString)) {
                    return characteristic
                }
                // 이거를 characterstic을 풀면 connection은 됨
                // 그런데 그러면 알맞은 charcteristic을 못 읽어옴
//                return characteristic
            }
            return null
        }

        /**
         * Match the given characteristic and a uuid string
         * @param characteristic one of found characteristic provided by the server
         * @param uuidString uuid as string to match
         * @return true if matched
         */
        private fun matchCharacteristic(
            characteristic: BluetoothGattCharacteristic?,
            uuidString: String
        ): Boolean {
            if (characteristic == null) {
                Log.d(TAG, "characteristic is null")
                return false
            }
            val uuid: UUID = characteristic.uuid
            Log.d(TAG, "uuid입니당: $uuid")
            return matchUUIDs(uuid.toString(), uuidString)
        }

        /**
         * Find Gatt service that matches with the server's service
         * @param serviceList list of services
         * @return matched service if found
         */
        private fun findGattService(serviceList: List<BluetoothGattService>): BluetoothGattService? {
            for (service in serviceList) {
                Log.d(TAG, "service: $service")
                val serviceUuidString = service.uuid.toString()
                Log.d(TAG, "service uuid: $serviceUuidString")
                if (matchServiceUUIDString(serviceUuidString)) {
                    return service
                }
//                return service
            }
            return null
        }

        /**
         * Try to match the given uuid with the service uuid
         * @param serviceUuidString service UUID as string
         * @return true if service uuid is matched
         */
        private fun matchServiceUUIDString(serviceUuidString: String): Boolean {
            Log.d(TAG, "match uuid: $serviceUuidString")
            // 여기서 서비스 uuid 따옴
            return matchUUIDs(serviceUuidString, "00001805-0000-1000-8000-00805f9b34fb")
        }

        /**
         * Check if there is any matching characteristic
         * @param characteristic query characteristic
         */
        // 이거 파악해봅시당!
        // 현재 여기서 막힘
        private fun isMatchingCharacteristic(characteristic: BluetoothGattCharacteristic?): Boolean {
            if (characteristic == null) {
                Log.d(TAG, "characteristic is null!!")
                return false
            }
            val uuid: UUID = characteristic.uuid
            // 이거가 리턴인지 봐야 할듯
            Log.d(TAG, matchCharacteristicUUID(uuid.toString()).toString())
            return matchCharacteristicUUID(uuid.toString())
        }

        /**
         * Query the given uuid as string to the provided characteristics by the server
         * @param characteristicUuidString query uuid as string
         * @return true if the matched is found
         */
        private fun matchCharacteristicUUID(characteristicUuidString: String): Boolean {
            return matchUUIDs(
                characteristicUuidString,
                CHARACTERISTIC_COMMAND_STRING,
                CHARACTERISTIC_RESPONSE_STRING
            )
        }

        /**
         * Try to match a uuid with the given set of uuid
         * @param uuidString uuid to query
         * @param matches a set of uuid
         * @return true if matched
         */
        private fun matchUUIDs(uuidString: String, vararg matches: String): Boolean {
            for (match in matches) {
                Log.d(TAG, "같은지 볼까유: $uuidString 이거와 $match")
                if (uuidString.equals(match, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }
}