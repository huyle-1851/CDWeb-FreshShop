function sort() {
    let combobox = document.getElementById("sort");
    let value = combobox.value;
    switch (value) {
        case "1":
            sortOrderByPriceASC();
            break;
        case "2":
            sortOrderByPriceDESC();
            break;
        case "3":
            sortOrderByNewStatus();
            break;
        case "4":
            sortOrderBySaleStatus();
            break;
        default:
            break;
    }
}

function getList() {
    let id = document.getElementsByClassName("id-param");
    let name = document.getElementsByClassName("name-param");
    let price = document.getElementsByClassName("price-param");
    let image = document.getElementsByClassName("image-param");
    let status = document.getElementsByClassName("status-param");

    let list = [];
    for (let i = 0; i < id.length; i++) {
        let product = {};
        product.id = id[i].getAttribute("href");
        product.name = name[i].innerHTML;
        product.price = price[i].innerHTML;
        product.image = image[i].getAttribute("src");
        product.status = status[i].innerHTML;

        list[i] = product;
    }
    return list;
}

function transform(list) {
    let id = document.getElementsByClassName("id-param");
    let name = document.getElementsByClassName("name-param");
    let price = document.getElementsByClassName("price-param");
    let image = document.getElementsByClassName("image-param");
    let status = document.getElementsByClassName("status-param");

    for (let i = 0; i < list.length; i++) {
        id[i].setAttribute("href", list[i].id);
        name[i].innerHTML = list[i].name;
        price[i].innerHTML = list[i].price;
        image[i].setAttribute("src", list[i].image);
        status[i].innerHTML = list[i].status;
    }
}

function sortOrderByPriceASC() {
    let list = getList();
    list = priceACS(list);
    transform(list);
}

function sortOrderByPriceDESC() {
    let list = getList();
    list = priceDECS(list);
    transform(list);
}

function sortOrderByNewStatus() {
    let list = getList();
    list = sortByStatus(list, "mới");
    transform(list);
}

function sortOrderBySaleStatus() {
    let list = getList();
    list = sortByStatus(list, "giảm giá");
    transform(list);
}

function priceACS(list) {
    let clone = list;
    for (let i = 0; i < clone.length - 1; i++) {
        for (let j = i + 1; j < clone.length; j++) {
            if (Number.parseInt(clone[i].price) > Number.parseInt(clone[j].price)) {
                let tmp = clone[i];
                clone[i] = clone[j];
                clone[j] = tmp;
            }
        }
    }
    list = clone;
    return list;
}

function priceDECS(list) {
    let clone = list;
    for (let i = 0; i < clone.length - 1; i++) {
        for (let j = i + 1; j < clone.length; j++) {
            if (Number.parseInt(clone[i].price) < Number.parseInt(clone[j].price)) {
                let tmp = clone[i];
                clone[i] = clone[j];
                clone[j] = tmp;
            }
        }
    }
    list = clone;
    return list;
}

function sortByStatus(list, status) {
    let clone = list;
    for (let i = 0; i < clone.length - 1; i++) {
        for (let j = i + 1; j < clone.length; j++) {
            let i_status = clone[i].status.toLowerCase().trim();
            let j_status = clone[j].status.toLowerCase().trim();
            let be_compared = status.toLowerCase().trim();
            if (i_status != be_compared && j_status == be_compared) {
                let tmp = clone[i];
                clone[i] = clone[j];
                clone[j] = tmp;
            }
        }
    }
    list = clone;
    return list;
}