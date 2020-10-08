/*
======================================================================
Funciones JavaScript para GTC
LAEFF Laboratorio de Astrofisica Espacial y Fisica Fundamental
Time:  2009-08-13
Author: Raul Gutierrez Sanchez
        raul@cab.inta-csic.es
        Tf.: 34.91.81.31.260
=====================================================================
*/

function mark_raw( formulario ) {
	
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == "prod_id" ){
			curElement.checked = formulario.markRaw.checked;
		}
	}
}

function mark_red( formulario ) {
	
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == "datablock" && curElement.value.indexOf("ALLRED")>0 ){
			curElement.checked = formulario.markRed.checked;
		}
	}
}

function mark_cal( formulario ) {
	
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == "datablock" && (curElement.value.indexOf("ALLCAL")>0 || curElement.value.indexOf("ALLCSS")>0) ){
			curElement.checked = formulario.markCal.checked;
		}
	}
}

function mark_ac( formulario ) {
	
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == "datablock" && curElement.value.indexOf("ALLAC")>0 ){
			curElement.checked = formulario.markAc.checked;
		}
	}
}

function mark_log( formulario ) {
	
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == "datablock" && curElement.value.indexOf("ALLLOG")>0 ){
			curElement.checked = formulario.markLog.checked;
		}
	}
}

function mark_prod( formulario ) {
	
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == "add" ){
			curElement.checked = formulario.markProd.checked;
		}
	}
}



/**
 * Gestiona el formuario de creacción de colecciones de datos para que nunca puedan especificarse 
 * tipos de reducciones de diferentes naturalezas.
 * @param formulario
 * @param curElement
 */
function newcol_update_form( formulario , curElement) {
	selImg=false;
	selImgOther=false;
	selSpec=false;
	selSpecOther=false;
	
	if( curElement.name == "newCollectionBean.redTypeImg" ){
		if(curElement.checked) selImg=true;
	}else if( curElement.name == "newCollectionBean.redTypeImgOther" ){
		selImgOther=true;
	}else if( curElement.name == "newCollectionBean.redTypeSpec" ){
		if(curElement.checked) selSpec=true;
	}else if( curElement.name == "newCollectionBean.redTypeSpecOther" ){
		selSpecOther=true;
	}

	if(selImg){
		newcol_desactiva_txt(formulario, "newCollectionBean.redTypeImgOther");
		newcol_desactiva_chk(formulario, "newCollectionBean.redTypeSpec");
		newcol_desactiva_txt(formulario, "newCollectionBean.redTypeSpecOther");
	}else if(selImgOther){
		newcol_desactiva_chk(formulario, "newCollectionBean.redTypeImg");
		newcol_desactiva_chk(formulario, "newCollectionBean.redTypeSpec");
		newcol_desactiva_txt(formulario, "newCollectionBean.redTypeSpecOther");
	}else if(selSpec){
		newcol_desactiva_chk(formulario, "newCollectionBean.redTypeImg");
		newcol_desactiva_txt(formulario, "newCollectionBean.redTypeImgOther");
		newcol_desactiva_txt(formulario, "newCollectionBean.redTypeSpecOther");
	}else if(selSpecOther){
		newcol_desactiva_chk(formulario, "newCollectionBean.redTypeImg");
		newcol_desactiva_txt(formulario, "newCollectionBean.redTypeImgOther");
		newcol_desactiva_chk(formulario, "newCollectionBean.redTypeSpec");
	}
	//alert("selImg= "+selImg+"; selImgOther="+selImgOther+"; selSpec="+selSpec+"; selSpecOther="+selSpecOther );
}

function newcol_desactiva_chk( formulario, name ) {
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == name ){
			curElement.checked=false;
		}
	}
}
function newcol_desactiva_txt( formulario, name ) {
	for( var i=0; i<formulario.elements.length; i++ ) {
		var curElement = formulario.elements[i];

		if( curElement.name == name ){
			curElement.value="";
		}
	}
}
