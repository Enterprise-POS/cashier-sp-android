package com.pos.cashiersp.use_case

import com.google.gson.Gson
import com.pos.cashiersp.common.HTTPStatus
import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.SignUpResponseDto
import com.pos.cashiersp.presentation.util.JwtStore
import com.pos.cashiersp.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class SignUpWithEmailAndPasswordRequest(private val repository: UserRepository, private val jwtStore: JwtStore) {
    operator fun invoke(email: String, password: String, name: String): Flow<Resource<SignUpResponseDto>> = flow {
        try {
            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                emit(Resource.Error("Email or password cannot be empty."))
                return@flow
            }
            emit(Resource.Loading<SignUpResponseDto>())
            val response = repository.signUpWithEmailAndPassword(email, password, name)
            if (!response.isSuccessful) {
                // Because Login route from the server is not protected with protected_route, 401 will not be included
                when (response.code()) {
                    400, 403 -> {
                        // Preferred to use .charStream instead .string because .string will safe the value into memory
                        // Which will throw error OutOfMemoryError
                        val reader = response.errorBody()!!.charStream()

                        // This will start parsing json
                        val errorResponse = Gson().fromJson(reader, HTTPStatus.ErrorResponse::class.java)

                        // Pass the error message and stop all flow here
                        emit(Resource.Error(errorResponse.message))
                        return@flow
                    }

                    // Fatal error or unexpected error code. If this error occurred, immediately contact / fix the server
                    else -> {
                        println("[INTERNAL ERROR] ${response.message()}")
                        println(response.errorBody())
                        emit(Resource.Error("[INTERNAL ERROR] Application Crashed"))
                        return@flow
                    }
                }
            }

            val successResponse = response.body()
            if (successResponse == null) {
                emit(Resource.Error("[FATAL ERROR] Empty JSON body"))
                return@flow
            }

            // 200
            var loginResponseDto = successResponse.data

            // Save the token to DataStore
            jwtStore.saveToken(loginResponseDto.token, loginResponseDto.user)

            emit(Resource.Success<SignUpResponseDto>(loginResponseDto))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        }
    }
}