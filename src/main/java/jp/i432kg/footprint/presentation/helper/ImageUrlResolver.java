package jp.i432kg.footprint.presentation.helper;

import jp.i432kg.footprint.domain.value.StorageObject;

public interface ImageUrlResolver {
    String resolve(StorageObject storageObject);
}