package org.guce.web.process.atm.services;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.atm.entities.ATMRegistration;
import org.guce.process.atm.repositories.impl.ATMRegistrationRepositoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ATMRegistrationServiceTest {
    protected static List<ATMRegistration> list;

    @Mock
    protected ATMRegistrationRepositoryImpl aTMRegistrationRepository;

    @Mock
    protected IdentifiantGeneratorLocal generator;

    @Mock
    protected CoreChargerFacadeLocal chargerFacade;

    @Mock
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @InjectMocks
    protected ATMRegistrationService instance;

    public ATMRegistrationServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ATMRegistration c = new ATMRegistration();
            c.setRecordId(1 + "");
            c.setRecordState("true");
            c.setOfficeCode(nextString(17));
            c.setAtmReference(nextString(17));
            c.setAtmIssueDate(GuceCalendarUtil.getCalendar().getTime());
            c.setAtmExpiryDate(GuceCalendarUtil.getCalendar().getTime());
            list.add(c);
        }
    }

    @Before
    public void setUp() {
        Mockito.when(aTMRegistrationRepository.count()).thenReturn(list.size());
        Mockito.when(aTMRegistrationRepository.findAll()).thenReturn(list);
        Mockito.when(aTMRegistrationRepository.search(ArgumentMatchers.any(SearchFilter.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(list);
        Mockito.when(aTMRegistrationRepository.searchCount(ArgumentMatchers.any(SearchFilter.class))).thenReturn(list.size());
    }

    @Test
    public void testCount() {
        System.out.println("count");
        int expResult = list.size();
        int result = instance.count();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindAll_0args() {
        System.out.println("findAll");
        List<ATMRegistration> expResult = list;
        List<ATMRegistration> result = instance.findAll();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSave_One() {
        System.out.println("save");
        ATMRegistration entity = list.get(0);
        entity.setRecordId("55");
        ATMRegistration expResult = entity;
        ATMRegistration result = instance.save(entity);
        Mockito.verify(aTMRegistrationRepository, Mockito.times(1)).edit(entity);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testRemove_One() {
        System.out.println("remove");
        ATMRegistration entity = list.get(0);
        instance.remove(entity);
        Mockito.verify(aTMRegistrationRepository, Mockito.times(1)).remove(entity);
    }

    @Test
    public void testRemove_List() {
        System.out.println("remove");
        instance.remove(list);
        Mockito.verify(aTMRegistrationRepository, Mockito.times(10)).remove(ArgumentMatchers.any(ATMRegistration.class));
    }

    @Test
    public void testSave_List() {
        System.out.println("save");
        instance.save(list);
        Mockito.verify(aTMRegistrationRepository, Mockito.times(10)).edit(ArgumentMatchers.any(ATMRegistration.class));
    }

    @Test
    public void testSearch() {
        System.out.println("search");
        SearchFilter filter = new SearchFilter();
        int start = 0;
        int count = 0;
        String orderField = "";
        String order = "";
        List<ATMRegistration> expResult = list;
        List<ATMRegistration> result = instance.search(filter, start, count, orderField, order);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSearchCount() {
        System.out.println("searchCount");
        SearchFilter filter = new SearchFilter();
        int expResult = list.size();
        int result = instance.searchCount(filter);
        Assert.assertEquals(expResult, result);
    }

    public static String nextString(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
