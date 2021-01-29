package ru.ovm.abin.returns

import com.fasterxml.jackson.annotation.JsonAutoDetect

/**
 * Created by vladislav
 * 10.02.2018
 * 8:38
 */

@JsonAutoDetect
enum class Result {
    SUCCESS,
    NOPASSWORD,
    INTERNALERROR,
    NOALBUM,
    NOGROUP,
    NOSELLER,
    NOITEM
}
