package com.lawmobile.body_cameras.enums

enum class XCameraCommandCodes(val commandValue: Int) {
    START_SESSION(0x101),
    RESET_VIEW_FINDER(0x103),
    VERIFY_DEVICE_INFO(0x00B),
    VERIFY_CLIENT_INFO(0x105),
    DOWNLOAD_FILE(0x505),
    TAKE_PHOTO(0x301),
    START_RECORD_VIDEO(0x201),
    STOP_RECORD_VIDEO(0x202),
    DISCONNECT_X_CAMERA(0x102),
    COMMAND_LS_IN_FOLDER(0x502),
    GET_MEDIA_INFO(0x402),
    UPLOAD_FILE(0x506),
    GET_SPACE(0X005),
    GET_BATTERY(0X00D),
    GET_NUM_FILES(0X006),
    DEL_FILE(0x501),
    GET_DIAGNOSIS(0x000e),
    GET_SETTING(0x001),
    COVERT_MODE_START(0x10000003),
    COVER_MODE_STOP(0x10000004),
    TURN_ON_BLUETOOTH(0x10000011),
    TURN_OFF_BLUETOOTH(0x10000012)
}