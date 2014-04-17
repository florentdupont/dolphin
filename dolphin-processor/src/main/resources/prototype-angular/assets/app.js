var app = angular.module('bootstrapDemoApp', ['ui.bootstrap', 'plunker', 'ngGrid'], function($httpProvider){
  delete $httpProvider.defaults.headers.common['X-Requested-With'];
});


function MainCtrl($scope, $http, $document, $modal, orderByFilter) {

 $scope.methodCount = 102;

  // données utiles pour le tableau
  $scope.myData = [{classe: "fr.rsi.gaya.entreprise.brs.service.v01_00.impl.EntrepriseBrsImpl", method: "controlerDonneesObligatoires", rule: "RG_0014", version:"2.x", status:"ONGOING"},
                     {classe: "fr.rsi.gaya.entreprise.brs.service.v01_00.impl.EntrepriseHorsFranceBrsImpl", method: "controlerCoherence", rule: "RG_0013s", version:"1.x", status:"TESTED"},
                     {classe: "MyClass", method: "test2", rule: "RG_003", version:"0.2", status:"DONE"},
                     {classe: "MyClass", method: "test2", rule: "RG_004", version:"0.2", status:"TODO"},
                     {classe: "MyClass", method: "test2", rule: "RG_005", version:"0.2", status:"ONGOING"}];


    $scope.gridOptions = { data: 'myData', 
                           enableRowSelection:false,
                           enableColumnResize:true,
                           columnDefs: [{field:'classe', displayName:'Classe'}, 
                                        {field:'method', displayName:'Méthode'},
                                        {field:'rule', displayName:'Règle', width:'80px'},
                                        {field:'version', displayName:'Version', width:'72px'},
                                        {field:'status', displayName:'Statut', cellTemplate:'<div ng-class="{blue: row.getProperty(col.field) == \'TESTED\',  green: row.getProperty(col.field) == \'DONE\', red: row.getProperty(col.field) == \'TODO\', orange: row.getProperty(col.field) == \'ONGOING\' }"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>', width:'72px'}
                                        ]
                         };

  
}

var ProgressCtrl = function ($scope) {
          $scope.randomStacked = function() {
            $scope.stacked = [{value:1, type:'danger'}, 
                              {value:3, type:'warning'},
                              {value:33, type:'success'},
                              {value:63, type:'info'}
                              ];
          };
       

          $scope.randomStacked();
      };


var TabsDemoCtrl = function ($scope) {
  $scope.tabs = [
    { title:"Dynamic Title 1", content:"Dynamic content 1" },
    { title:"Dynamic Title 2", content:"Dynamic content 2", disabled: true }
  ];

  $scope.alertMe = function() {
    setTimeout(function() {
      alert("You've selected the alert tab!");
    });
  };

  $scope.navType = 'pills';
};


       
