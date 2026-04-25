package org.example.testinglab1.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.testinglab1.dto.filter.ProductFilter;
import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.request.UpdateProductRequest;
import org.example.testinglab1.dto.response.ProductFullDto;
import org.example.testinglab1.dto.response.ProductPageDto;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.exception.InvalidMacroRatioException;
import org.example.testinglab1.exception.NotFoundException;
import org.example.testinglab1.mapper.ProductMapper;
import org.example.testinglab1.repository.ProductRepository;
import org.example.testinglab1.service.ProductService;
import org.example.testinglab1.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductSpecification productSpecification;

    @Override
    public UUID create(CreateProductRequest request){
        if(request.getProteins() + request.getFats() + request.getCarbohydrates() > 100){
            throw new InvalidMacroRatioException("Сумма бжу больше 100 грамм");
        }

        Product product = productMapper.toEntity(request);

        productRepository.save(product);

        return product.getId();
    }

    @Override
    public ProductPageDto getAll(Pageable pageable, ProductFilter filter){
        Page<Product> page = productRepository
                .findAll(productSpecification.getSpecification(filter), pageable);

        return productMapper.toPageDto(page);
    }

    @Override
    public ProductFullDto getById(UUID id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id: " + id + " не найден"));

        return productMapper.toFullDto(product);
    }

    @Override
    public void deleteById(UUID id){
        productRepository.deleteById(id);
    }

    @Override
    public void updateById(UUID id, UpdateProductRequest request){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Продукт с id: " + id + " не найден"));

        Double proteins = request.getProteins() != null ? request.getProteins() : product.getProteins();
        Double fats = request.getFats() != null ? request.getFats() : product.getFats();
        Double carbohydrates = request.getCarbohydrates() != null ? request.getCarbohydrates() : product.getCarbohydrates();

        if(proteins + fats + carbohydrates > 100){
            throw new InvalidMacroRatioException("Сумма бжу больше 100");
        }

        if (request.getName() != null){
            product.setName(request.getName());
        }
        if (request.getPhotos() != null){
            product.setPhotos(request.getPhotos());
        }
        if (request.getCalories() != null){
            product.setCalories(request.getCalories());
        }
        if (request.getProteins() != null){
            product.setProteins(request.getProteins());
        }
        if (request.getFats() != null){
            product.setFats(request.getFats());
        }
        if (request.getCarbohydrates() != null){
            product.setCarbohydrates(request.getCarbohydrates());
        }
        if (request.getComposition() != null){
            product.setComposition(request.getComposition());
        }
        if (request.getCategory() != null){
            product.setCategory(request.getCategory());
        }
        if (request.getReadiness() != null){
            product.setReadiness(request.getReadiness());
        }
        if (request.getFlags() != null){
            product.setFlags(request.getFlags());
        }

        productRepository.save(product);
    }
}
