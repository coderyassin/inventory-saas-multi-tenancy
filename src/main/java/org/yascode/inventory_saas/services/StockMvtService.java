package org.yascode.inventory_saas.services;

import org.yascode.inventory_saas.common.PageResponse;
import org.yascode.inventory_saas.requests.StockMvtRequest;
import org.yascode.inventory_saas.responses.StockMvtResponse;

public interface StockMvtService extends BasicService<StockMvtRequest, StockMvtResponse> {

    PageResponse<StockMvtResponse> findAllByProductId(final String productId, final int page, final int size);
}
