package com.pos.cashiersp.use_case

import com.pos.cashiersp.common.Resource
import com.pos.cashiersp.model.dto.SignUpResponseDto
import com.pos.cashiersp.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class SignUpWithEmailAndPasswordRequest(private val repository: UserRepository) {
    operator fun invoke(): Flow<Resource<SignUpResponseDto>> = flow {
        try {

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "[INTERNAL ERROR] An unexpected error occurred"))
        } catch (e: IOException) {
            println("IOException message: ${e.message}")
            emit(Resource.Error("[INTERNAL ERROR] Couldn't reach server. Check your internet connection."))
        }
    }
}