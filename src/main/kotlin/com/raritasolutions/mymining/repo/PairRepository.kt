package com.raritasolutions.mymining.repo

import com.raritasolutions.mymining.model.PairRecord
import org.springframework.data.repository.CrudRepository

interface PairRepository:CrudRepository<PairRecord,Int>